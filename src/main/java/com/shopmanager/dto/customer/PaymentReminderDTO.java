package com.shopmanager.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentReminderDTO {

    private String whatsappLink;

    private Long customerId;
    private String name;
    private String phone;
    private BigDecimal pendingAmount;
    private String message;


    public PaymentReminderDTO(Long customerId, String name, String phone,
                              BigDecimal pendingAmount, String message,
                              String whatsappLink) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.pendingAmount = pendingAmount;
        this.message = message;
        this.whatsappLink = whatsappLink;
    }

}