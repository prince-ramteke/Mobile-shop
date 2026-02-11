package com.shopmanager.dto.repair;

import com.shopmanager.entity.enums.RepairStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepairJobRequest {

    @NotNull
    private Long customerId;

    @NotBlank
    private String deviceBrand;

    @NotBlank
    private String deviceModel;

    private String imei;

    @NotBlank
    private String issueDescription;

    private BigDecimal estimatedCost;

    private BigDecimal finalCost;

    private BigDecimal advancePaid;

    private RepairStatus status;

}