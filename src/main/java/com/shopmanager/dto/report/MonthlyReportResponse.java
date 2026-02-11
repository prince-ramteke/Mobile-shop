package com.shopmanager.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class MonthlyReportResponse {
    private int year;
    private int month;
    private BigDecimal totalSales;
    private Long invoiceCount;
    private BigDecimal totalTax;
}