package com.shopmanager.service.impl;

import com.shopmanager.dto.payment.PaymentRequest;
import com.shopmanager.dto.payment.PaymentResponse;
import com.shopmanager.entity.Payment;
import com.shopmanager.entity.Sale;
import com.shopmanager.entity.enums.DueReferenceType;
import com.shopmanager.repository.PaymentRepository;
import com.shopmanager.repository.SaleRepository;
import com.shopmanager.service.DueService;
import com.shopmanager.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;
    private final DueService dueService;

    @Override
    @Transactional
    public PaymentResponse addPayment(PaymentRequest request) {

        Sale sale = saleRepository.findById(request.getSaleId())
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        Payment payment = Payment.builder()
                .sale(sale)
                .amount(request.getAmount())
                .paymentMode(request.getPaymentMode())
                .referenceNote(request.getReferenceNote())
                .paidAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        sale.setAmountReceived(
                sale.getAmountReceived().add(request.getAmount())
        );
        sale.setPendingAmount(
                sale.getGrandTotal().subtract(sale.getAmountReceived())
        );
        saleRepository.save(sale);

        dueService.addPayment(
                DueReferenceType.SALE,
                sale.getId(),
                request.getAmount()
        );

        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setSaleId(sale.getId());
        response.setAmount(payment.getAmount());
        response.setPaymentMode(payment.getPaymentMode());
        response.setReferenceNote(payment.getReferenceNote());
        response.setPaidAt(payment.getPaidAt().toString());

        return response;
    }

    @Override
    public List<PaymentResponse> getPaymentsBySale(Long saleId) {
        return paymentRepository.findBySaleId(saleId)
                .stream()
                .map(payment -> {
                    PaymentResponse r = new PaymentResponse();
                    r.setId(payment.getId());
                    r.setSaleId(payment.getSale().getId());
                    r.setAmount(payment.getAmount());
                    r.setPaymentMode(payment.getPaymentMode());
                    r.setReferenceNote(payment.getReferenceNote());
                    r.setPaidAt(payment.getPaidAt().toString());
                    return r;
                })
                .toList();
    }
}