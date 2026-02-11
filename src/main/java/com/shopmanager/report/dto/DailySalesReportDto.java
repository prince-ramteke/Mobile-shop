package com.shopmanager.report.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class DailySalesReportDto {
    private LocalDate date;
    private BigDecimal totalSales;
    private int invoiceCount;
    private BigDecimal totalTax;
    private BigDecimal cashSales;
    private BigDecimal creditSales;

    // Additional fields for PDF/Excel generators
    private BigDecimal totalReceived;
    private BigDecimal totalPending;

    // Alias method for PDF/Excel generators looking for getTotalInvoices()
    public int getTotalInvoices() {
        return invoiceCount;
    }

    // Alias methods for PDF/Excel generators
    public BigDecimal getTotalReceived() {
        return totalReceived != null ? totalReceived : BigDecimal.ZERO;
    }

    public BigDecimal getTotalPending() {
        return totalPending != null ? totalPending : BigDecimal.ZERO;
    }
}