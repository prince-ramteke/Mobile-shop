package com.shopmanager.dto.payment;

import com.shopmanager.entity.enums.PaymentMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull
    private Long saleId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private PaymentMode paymentMode;

    private String referenceNote;
}