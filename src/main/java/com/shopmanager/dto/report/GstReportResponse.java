package com.shopmanager.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class GstReportResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal cgstTotal;
    private BigDecimal sgstTotal;
    private BigDecimal igstTotal;
    private BigDecimal totalTax;
}