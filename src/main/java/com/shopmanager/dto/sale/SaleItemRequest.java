package com.shopmanager.dto.sale;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleItemRequest {

    private String itemName;
    private Integer quantity;
    private BigDecimal price;
    private String type; // Accessory / Service / Repair
}