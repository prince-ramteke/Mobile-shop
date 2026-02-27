package com.shopmanager.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SalesSummaryResponse {

    private String period;
    private BigDecimal totalSales;
    private Long invoiceCount;
}