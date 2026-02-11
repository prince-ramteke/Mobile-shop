package com.shopmanager.dto.report;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class DateRangeReportResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalSales;
    private int invoiceCount;
    private BigDecimal totalTax;
}