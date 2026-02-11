package com.shopmanager.dto.customer;

import lombok.Data;

@Data
public class CustomerResponse {

    private Long id;
    private String name;
    private String phone;
    private String whatsappNumber;
    private String email;
    private String address;
    private String createdAt;

}