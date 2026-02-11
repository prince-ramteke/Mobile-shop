package com.shopmanager.dto.sale;

import com.shopmanager.entity.enums.GstType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SaleRequest {

    private Long customerId;
    private LocalDate saleDate;

    private BigDecimal gstRate;
    private GstType gstType;

    private BigDecimal amountReceived;
    private String paymentMode;

    private String notes;

    private List<SaleItemRequest> items;
}