package com.shopmanager.dto.product;

import com.shopmanager.entity.enums.ProductCategory;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {

    private Long id;
    private String name;
    private String brand;
    private ProductCategory category;
    private String imei;
    private BigDecimal sellingPrice;
    private BigDecimal costPrice;
    private Integer stock;
    private String createdAt;
    private String model;

    private Boolean gstApplicable;
    private BigDecimal gstPercent;


}