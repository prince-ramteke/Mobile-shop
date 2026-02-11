package com.shopmanager.service;

import com.shopmanager.dto.payment.PaymentRequest;
import com.shopmanager.dto.payment.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse addPayment(PaymentRequest request);

    List<PaymentResponse> getPaymentsBySale(Long saleId);
}