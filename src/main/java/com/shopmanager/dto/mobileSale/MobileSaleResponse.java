package com.shopmanager.dto.mobileSale;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MobileSaleResponse {

    private Long id;

    private String company;
    private String model;
    private String imei1;
    private String imei2;

    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private BigDecimal advancePaid;
    private BigDecimal pendingAmount;

    private Integer warrantyYears;
    private LocalDate warrantyExpiry;

    private LocalDateTime createdAt;

    // 👇 CUSTOMER DETAILS
    private String customerName;
    private String customerPhone;
    private String customerAddress;

    private Long customerId;

    private Boolean emiEnabled;
    private Integer emiMonths;
    private Double emiInterestRate;
    private Double emiProcessingFee;
    private Double emiInterestAmount;
    private Double emiTotalPayable;
    private Double emiMonthlyAmount;

}