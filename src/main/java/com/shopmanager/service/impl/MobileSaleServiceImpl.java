package com.shopmanager.service.impl;

import com.shopmanager.dto.mobileSale.MobileSaleRequest;
import com.shopmanager.entity.Customer;
import com.shopmanager.entity.MobileSale;
import com.shopmanager.repository.CustomerRepository;
import com.shopmanager.repository.MobileSaleRepository;
import com.shopmanager.service.MobileSaleService;
import com.shopmanager.service.PdfService;
import com.shopmanager.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MobileSaleServiceImpl implements MobileSaleService {

    private final PdfService pdfService;

    private final MobileSaleRepository saleRepository;
    private final CustomerRepository customerRepository;

    private final WhatsAppService whatsAppService;


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

// 🔔 WhatsApp Hook (Meta integration later)
        try {
            whatsAppService.sendInvoice(saved.getId());
        } catch (Exception e) {
            System.out.println("WhatsApp hook failed: " + e.getMessage());
        }

        return saved.getId();


    }

}