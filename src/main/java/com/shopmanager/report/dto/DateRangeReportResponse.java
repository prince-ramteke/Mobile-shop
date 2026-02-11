package com.shopmanager.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeReportResponse {
    private LocalDate fromDate;
    private LocalDate toDate;
    private long totalInvoices;
    private BigDecimal subTotal;
    private BigDecimal totalTax;
    private BigDecimal grandTotal;
    private BigDecimal amountReceived;
    private BigDecimal pendingAmount;
}