package com.shopmanager.report.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class MonthlySalesReportDto {
    private int year;
    private int month;
    private String monthName;
    private BigDecimal totalSales;
    private int invoiceCount;
    private BigDecimal totalTax;
    private BigDecimal averageDailySales;
}