package com.shopmanager.service.impl;

import com.shopmanager.dto.CustomerLedgerRow;
import com.shopmanager.dto.mobileSale.MobileSaleRequest;
import com.shopmanager.dto.mobileSale.MobileSaleResponse;
import com.shopmanager.entity.Customer;
import com.shopmanager.entity.MobileSale;
import com.shopmanager.repository.CustomerRepository;
import com.shopmanager.repository.MobileSaleRepository;
import com.shopmanager.service.MobileSaleService;
import com.shopmanager.service.PdfService;
import com.shopmanager.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.shopmanager.entity.MobileSaleRecovery;
import com.shopmanager.repository.MobileSaleRecoveryRepository;

import java.util.ArrayList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MobileSaleServiceImpl implements MobileSaleService {

    private final PdfService pdfService;

    private final MobileSaleRepository saleRepository;
    private final MobileSaleRecoveryRepository recoveryRepository;

    private final CustomerRepository customerRepository;

    private final WhatsAppService whatsAppService;


    @Override
    public List<MobileSaleResponse> search(String txt) {

        if (txt == null || txt.trim().isEmpty()) {
            return getAll();
        }

        return saleRepository.search(txt)
                .stream()
                .map(sale -> {

                    Customer customer = customerRepository
                            .findById(sale.getCustomerId())
                            .orElse(null);

                    return MobileSaleResponse.builder()
                            .id(sale.getId())
                            .company(sale.getCompany())
                            .model(sale.getModel())
                            .imei1(sale.getImei1())
                            .imei2(sale.getImei2())
                            .quantity(sale.getQuantity())
                            .price(sale.getPrice())
                            .totalAmount(sale.getTotalAmount())
                            .advancePaid(sale.getAdvancePaid())
                            .pendingAmount(sale.getPendingAmount())
                            .warrantyYears(sale.getWarrantyYears())
                            .warrantyExpiry(sale.getWarrantyExpiry())
                            .createdAt(sale.getCreatedAt())
                            .customerId(customer != null ? customer.getId() : null)
                            .customerName(customer != null ? customer.getName() : "")
                            .customerPhone(customer != null ? customer.getPhone() : "")
                            .customerAddress(customer != null ? customer.getAddress() : "")
                            .build();
                })
                .toList();
    }

    @Override
    public List<MobileSaleResponse> getAll() {

        return saleRepository.findAllByOrderByIdDesc()
                .stream()
                .map(sale -> {

                    Customer customer = customerRepository
                            .findById(sale.getCustomerId())
                            .orElse(null);

                    return MobileSaleResponse.builder()
                            .id(sale.getId())
                            .company(sale.getCompany())
                            .model(sale.getModel())
                            .imei1(sale.getImei1())
                            .imei2(sale.getImei2())
                            .quantity(sale.getQuantity())
                            .price(sale.getPrice())
                            .totalAmount(sale.getTotalAmount())
                            .advancePaid(sale.getAdvancePaid())
                            .pendingAmount(sale.getPendingAmount())
                            .warrantyYears(sale.getWarrantyYears())
                            .warrantyExpiry(sale.getWarrantyExpiry())
                            .createdAt(sale.getCreatedAt())
                            .customerName(customer != null ? customer.getName() : "")
                            .customerPhone(customer != null ? customer.getPhone() : "")
                            .customerAddress(customer != null ? customer.getAddress() : "")
                            .customerId(customer != null ? customer.getId() : null)
                            .build();
                })
                .toList();
    }



    @Override
    public MobileSaleResponse getById(Long id) {

        MobileSale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mobile sale not found"));

        Customer customer = customerRepository.findById(sale.getCustomerId())
                .orElse(null);

        return MobileSaleResponse.builder()
                .id(sale.getId())
                .company(sale.getCompany())
                .model(sale.getModel())
                .imei1(sale.getImei1())
                .imei2(sale.getImei2())
                .quantity(sale.getQuantity())
                .price(sale.getPrice())
                .totalAmount(sale.getTotalAmount())
                .advancePaid(sale.getAdvancePaid())
                .pendingAmount(sale.getPendingAmount())
                .warrantyYears(sale.getWarrantyYears())
                .warrantyExpiry(sale.getWarrantyExpiry())
                .createdAt(sale.getCreatedAt())
                .customerName(customer != null ? customer.getName() : "")
                .customerPhone(customer != null ? customer.getPhone() : "")
                .customerAddress(customer != null ? customer.getAddress() : "")
                .customerId(customer != null ? customer.getId() : null)
                .build();
    }



    @Override
    public Long createSale(MobileSaleRequest req) {

        // ✅ FIX: reuse customer if phone exists
        Customer customer = customerRepository
                .findByPhone(req.getCustomerPhone())
                .orElseGet(() -> {
                    Customer c = Customer.builder()
                            .name(req.getCustomerName())
                            .phone(req.getCustomerPhone())
                            .address(req.getCustomerAddress())
                            .build();
                    return customerRepository.save(c);
                });

        BigDecimal total = req.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
        BigDecimal pending = total.subtract(req.getAdvancePaid());
        if (pending.compareTo(BigDecimal.ZERO) < 0) {
            pending = BigDecimal.ZERO;
        }

        LocalDate expiry = LocalDate.now().plusYears(req.getWarrantyYears());

        MobileSale sale = MobileSale.builder()
                .company(req.getCompany())
                .model(req.getModel())
                .imei1(req.getImei1())
                .imei2(req.getImei2())
                .quantity(req.getQuantity())
                .price(req.getPrice())
                .totalAmount(total)
                .advancePaid(req.getAdvancePaid())
                .pendingAmount(pending)
                .warrantyYears(req.getWarrantyYears())
                .warrantyExpiry(expiry)
                .customerId(customer.getId())
                .createdAt(LocalDateTime.now())
                .build();

        MobileSale saved = saleRepository.save(sale);

// Generate PDF (for later download + WhatsApp)
        pdfService.generateMobileSaleInvoicePdf(saved.getId());

        try {
            whatsAppService.sendInvoice(saved.getId());
        } catch (Exception e) {
            System.out.println("WhatsApp hook failed: " + e.getMessage());
        }


        return saved.getId();


    }


    @Override
    public void recoverPayment(Long id, Double amount) {

        MobileSale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        BigDecimal recover = BigDecimal.valueOf(amount);

        if (recover.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid recover amount");
        }

        if (recover.compareTo(sale.getPendingAmount()) > 0) {
            throw new RuntimeException("Recover amount exceeds pending");
        }

        BigDecimal pending = sale.getPendingAmount().subtract(recover);


        if (pending.compareTo(BigDecimal.ZERO) < 0) {
            pending = BigDecimal.ZERO;
        }

        sale.setPendingAmount(pending);
        saleRepository.save(sale);

        // Save recovery history
        MobileSaleRecovery rec = new MobileSaleRecovery();
        rec.setSaleId(sale.getId());
        rec.setAmount(amount);
        rec.setPendingAfter(sale.getPendingAmount().doubleValue());
        recoveryRepository.save(rec);

    }


    @Override
    public Long updateSale(Long id, MobileSaleRequest req) {

        MobileSale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        // Reuse customer by phone
        Customer customer = customerRepository
                .findByPhone(req.getCustomerPhone())
                .orElseGet(() -> {
                    Customer c = Customer.builder()
                            .name(req.getCustomerName())
                            .phone(req.getCustomerPhone())
                            .address(req.getCustomerAddress())
                            .build();
                    return customerRepository.save(c);
                });

        BigDecimal total = req.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
        BigDecimal pending = total.subtract(req.getAdvancePaid());
        if (pending.compareTo(BigDecimal.ZERO) < 0) pending = BigDecimal.ZERO;

        LocalDate expiry = LocalDate.now().plusYears(req.getWarrantyYears());

        // Update fields
        sale.setCompany(req.getCompany());
        sale.setModel(req.getModel());
        sale.setImei1(req.getImei1());
        sale.setImei2(req.getImei2());
        sale.setQuantity(req.getQuantity());
        sale.setPrice(req.getPrice());
        sale.setTotalAmount(total);
        sale.setAdvancePaid(req.getAdvancePaid());
        sale.setPendingAmount(pending);
        sale.setWarrantyYears(req.getWarrantyYears());
        sale.setWarrantyExpiry(expiry);
        sale.setCustomerId(customer.getId());

        saleRepository.save(sale);

        return sale.getId();
    }
    @Override
    public List<MobileSale> getPendingSales() {
        return saleRepository.findByPendingAmountGreaterThanOrderByCreatedAtDesc(BigDecimal.ZERO);
    }


    @Override
    public List<CustomerLedgerRow> getCustomerLedger(Long customerId) {

        List<MobileSale> sales = saleRepository.findAll()
                .stream()
                .filter(s -> s.getCustomerId().equals(customerId))
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt())) // oldest first
                .toList();


        List<CustomerLedgerRow> rows = new ArrayList<>();

        double balance = 0;

        for (MobileSale sale : sales) {

            double total = sale.getTotalAmount().doubleValue();
            balance += total;

            rows.add(CustomerLedgerRow.builder()
                    .type("SALE")
                    .saleId(sale.getId())
                    .debit(total)
                    .credit(0.0)
                    .balance(balance)
                    .date(sale.getCreatedAt())
                    .build());

            List<MobileSaleRecovery> recs =
                    recoveryRepository.findBySaleIdOrderByCreatedAtDesc(sale.getId());

            for (MobileSaleRecovery r : recs) {
                balance -= r.getAmount();
                if (balance < 0) balance = 0;

                rows.add(CustomerLedgerRow.builder()
                        .type("RECOVERY")
                        .saleId(sale.getId())
                        .debit(0.0)
                        .credit(r.getAmount())
                        .balance(balance)
                        .date(r.getCreatedAt())
                        .build());
            }
        }

        rows.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return rows;
    }



}