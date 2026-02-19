package com.shopmanager.dto.mobileSale;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MobileSaleRequest {

    private String company;
    private String model;

    private String imei1;
    private String imei2;

    private Integer quantity;
    private BigDecimal price;

    private BigDecimal advancePaid;

    private Integer warrantyYears;

    private String customerName;
    private String customerPhone;
    private String customerAddress;

    private Boolean emiEnabled;

    private Integer emiMonths;

    private Double emiInterestRate;

    private Double emiProcessingFee;

}