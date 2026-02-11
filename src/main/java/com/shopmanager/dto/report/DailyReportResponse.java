package com.shopmanager.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class DailyReportResponse {
    private LocalDate date;
    private BigDecimal totalSales;
    private Long invoiceCount;
    private BigDecimal totalTax;
}