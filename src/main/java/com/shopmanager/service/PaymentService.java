package com.shopmanager.service;

import com.shopmanager.dto.payment.PaymentRequest;
import com.shopmanager.dto.payment.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse addPayment(PaymentRequest request);
    List<com.shopmanager.entity.due.DuePayment> getDuePaymentsByCustomer(Long customerId);

    List<PaymentResponse> getPaymentsBySale(Long saleId);
}