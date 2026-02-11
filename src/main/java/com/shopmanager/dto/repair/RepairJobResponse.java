package com.shopmanager.dto.repair;

import com.shopmanager.entity.enums.RepairStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepairJobResponse {

    private Long id;
    private String jobNumber;

    // Customer details
    private Long customerId;
    private String customerName;
    private String customerPhone;

    // Device details
    private String deviceBrand;
    private String deviceModel;
    private String imei;

    // Problem and costs
    private String issueDescription;
    private BigDecimal estimatedCost;
    private BigDecimal finalCost;
    private BigDecimal advancePaid;
    private BigDecimal pendingAmount;

    // Status
    private RepairStatus status;

    // Timestamps
    private String createdAt;
    private String deliveredAt;
}