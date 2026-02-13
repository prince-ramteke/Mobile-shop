package com.shopmanager.service.impl;

import com.shopmanager.audit.service.AuditService;
import com.shopmanager.dto.sale.*;
import com.shopmanager.entity.*;
import com.shopmanager.entity.enums.*;
import com.shopmanager.exception.ResourceNotFoundException;
import com.shopmanager.mapper.SaleMapper;
import com.shopmanager.message.dto.MessagePayload;
import com.shopmanager.message.service.NotificationService;
import com.shopmanager.repository.CustomerRepository;
import com.shopmanager.repository.MessageLogRepository;
import com.shopmanager.repository.SaleRepository;
import com.shopmanager.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final MessageLogRepository messageLogRepository;

    private final AuditService auditService;
    private final SaleMapper saleMapper;
    private final InvoiceNumberService invoiceNumberService;
    private final TaxCalculatorService taxCalculatorService;
    private final PdfService pdfService;
    private final NotificationService notificationService;
    private final DueService dueService;

    // =====================================================
    // CREATE SALE - WITH VALIDATION FIXES
    // =====================================================
    @Override
    @Transactional
    public SaleResponse createSale(SaleRequest request) {

        // ✅ VALIDATION
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one item is required");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setInvoiceNumber(invoiceNumberService.generateInvoiceNumber());
        sale.setSaleDate(
                request.getSaleDate() != null ? request.getSaleDate() : LocalDate.now()
        );

        // ✅ FIX: Ensure GST rate is BigDecimal
        BigDecimal gstRate = request.getGstRate() != null
                ? request.getGstRate()
                : new BigDecimal("18");
        sale.setGstRate(gstRate);

        sale.setGstType(request.getGstType() != null ? request.getGstType() : GstType.CGST_SGST);

        BigDecimal subTotal = BigDecimal.ZERO;

        // ✅ PROCESS ITEMS WITH VALIDATION
        for (int i = 0; i < request.getItems().size(); i++) {
            SaleItemRequest itemReq = request.getItems().get(i);

            // Validate item
            if (itemReq.getItemName() == null || itemReq.getItemName().trim().isEmpty()) {
                throw new IllegalArgumentException("Item name is required for item " + (i + 1));
            }
            if (itemReq.getPrice() == null || itemReq.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valid price is required for item " + (i + 1));
            }
            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new IllegalArgumentException("Valid quantity is required for item " + (i + 1));
            }

            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setItemName(itemReq.getItemName().trim());
            item.setType(itemReq.getType() != null ? itemReq.getType() : "Accessory");
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getPrice());

            // ✅ FIX: Calculate line total manually to ensure precision
            BigDecimal lineTotal = itemReq.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            item.setLineTotal(lineTotal);

            subTotal = subTotal.add(lineTotal);
            sale.getItems().add(item);
        }

        sale.setSubTotal(subTotal);

        // ✅ FIX: Calculate taxes manually if taxCalculatorService fails
        try {
            taxCalculatorService.calculateTaxes(sale);
        } catch (Exception e) {
            // Fallback calculation
            BigDecimal gstAmount = subTotal.multiply(gstRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            if ("CGST_SGST".equals(sale.getGstType())) {
                sale.setCgstAmount(gstAmount.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP));
                sale.setSgstAmount(gstAmount.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP));
                sale.setIgstAmount(BigDecimal.ZERO);
            } else {
                sale.setIgstAmount(gstAmount);
                sale.setCgstAmount(BigDecimal.ZERO);
                sale.setSgstAmount(BigDecimal.ZERO);
            }
            sale.setTotalTax(gstAmount);
            sale.setGrandTotal(subTotal.add(gstAmount));
        }

        // ✅ FIX: Handle amount received properly
        BigDecimal received = request.getAmountReceived() != null
                ? request.getAmountReceived()
                : BigDecimal.ZERO;

        // Ensure received doesn't exceed grand total
        if (received.compareTo(sale.getGrandTotal()) > 0) {
            received = sale.getGrandTotal();
        }

        sale.setAmountReceived(received);
        sale.setPendingAmount(sale.getGrandTotal().subtract(received));

        // ✅ FIX: Handle payment mode
        try {
            sale.setPaymentMode(
                    request.getPaymentMode() != null
                            ? PaymentMode.valueOf(request.getPaymentMode())
                            : PaymentMode.CASH
            );
        } catch (IllegalArgumentException e) {
            sale.setPaymentMode(PaymentMode.CASH);
        }

        sale.setNotes(request.getNotes() != null ? request.getNotes().trim() : "");

        // ✅ SAVE SALE
        Sale savedSale = saleRepository.save(sale);

        // ✅ AUDIT LOG
        auditService.log(
                "CREATE_SALE",
                "SALE",
                savedSale.getId(),
                "Invoice " + savedSale.getInvoiceNumber()
        );

        // ✅ CREATE DUE ENTRY
        try {
            dueService.createDue(
                    customer.getId(),
                    DueReferenceType.SALE,
                    savedSale.getId(),
                    savedSale.getGrandTotal(),
                    savedSale.getAmountReceived()
            );
        } catch (Exception e) {
            System.err.println("Due creation failed (non-critical): " + e.getMessage());
        }

        // ✅ AUTO WHATSAPP (NON-CRITICAL - don't fail if this fails)
        try {
            byte[] pdf = pdfService.generateSaleInvoicePdf(savedSale.getId());

            MessageLog log = MessageLog.builder()
                    .customer(customer)
                    .type(MessageType.BILL)
                    .channel(MessageChannel.WHATSAPP)
                    .status(MessageStatus.PENDING)
                    .messageContent("Invoice for Sale " + savedSale.getInvoiceNumber())
                    .build();

            messageLogRepository.save(log);

            MessagePayload payload = MessagePayload.builder()
                    .recipient(customer.getWhatsappNumber())
                    .message(
                            "Thank you for shopping with us!\n" +
                                    "Invoice No: " + savedSale.getInvoiceNumber() + "\n" +
                                    "Total: ₹" + savedSale.getGrandTotal()
                    )
                    .pdf(pdf)
                    .fileName("Invoice-" + savedSale.getInvoiceNumber() + ".pdf")
                    .build();

            notificationService.send(log, payload);

        } catch (Exception e) {
            System.err.println("WhatsApp notification failed (non-critical): " + e.getMessage());
        }

        return saleMapper.toResponse(savedSale);
    }

    // =====================================================
    // UPDATE SALE
    // =====================================================
    @Override
    @Transactional
    public SaleResponse updateSale(Long id, SaleRequest request) {
        deleteSale(id);
        return createSale(request);
    }

    // =====================================================
    // GET SALE - WITH EAGER LOADING FIX
    // =====================================================
    @Override
    @Transactional(readOnly = true)
    public SaleResponse getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        // ✅ FIX: Force load lazy collections to avoid LazyInitializationException
        if (sale.getCustomer() != null) {
            sale.getCustomer().getName();
            sale.getCustomer().getPhone();
            sale.getCustomer().getAddress();
        }
        if (sale.getItems() != null) {
            sale.getItems().size(); // Force initialization
        }

        return saleMapper.toResponse(sale);
    }

    // =====================================================
    // LIST SALES
    // =====================================================
    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponse> listSales(String start, String end, Boolean pendingOnly, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Sale> sales = saleRepository.findSales(
                start != null ? LocalDate.parse(start) : null,
                end != null ? LocalDate.parse(end) : null,
                pendingOnly,
                pageable
        );

        // ✅ FIX: Initialize lazy collections for each sale
        sales.getContent().forEach(sale -> {
            if (sale.getCustomer() != null) sale.getCustomer().getName();
            if (sale.getItems() != null) sale.getItems().size();
        });

        return sales.map(saleMapper::toResponse);
    }

    // =====================================================
    // DELETE SALE
    // =====================================================
    @Override
    @Transactional
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        saleRepository.delete(sale);
    }
}