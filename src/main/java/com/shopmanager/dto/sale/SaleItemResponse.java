package com.shopmanager.dto.sale;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleItemResponse {
    private Long id;
    private String itemName;
    private String type;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}