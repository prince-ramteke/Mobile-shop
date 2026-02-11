package com.shopmanager.dto.payment;

import com.shopmanager.entity.enums.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentResponse {

    private Long id;
    private Long saleId;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private String referenceNote;
    private String paidAt;
}