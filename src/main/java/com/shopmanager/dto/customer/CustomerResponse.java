package com.shopmanager.dto.customer;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerResponse {

    private BigDecimal dueAmount;

    private Long id;
    private String name;
    private String phone;
    private String whatsappNumber;
    private String email;
    private String address;
    private String createdAt;

}