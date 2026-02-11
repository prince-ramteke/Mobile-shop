package com.shopmanager.dto.product;

import com.shopmanager.entity.enums.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank
    private String name;

    private String brand;

    @NotNull
    private ProductCategory category;

    private String imei;

    @NotNull
    private BigDecimal sellingPrice;

    private BigDecimal costPrice;

    private Integer stock;

    private Boolean gstApplicable;
    private BigDecimal gstPercent;

}