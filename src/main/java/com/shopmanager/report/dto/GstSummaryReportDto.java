package com.shopmanager.report.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class GstSummaryReportDto {
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal cgstTotal;
    private BigDecimal sgstTotal;
    private BigDecimal igstTotal;
    private BigDecimal totalTax;
    private BigDecimal taxableAmount;
    private int totalInvoices;
}