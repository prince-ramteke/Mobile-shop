package com.shopmanager.report.dto;

import java.math.BigDecimal;

public record MonthlyReportResponse(
        int year,
        int month,
        long totalInvoices,
        BigDecimal subTotal,
        BigDecimal totalTax,
        BigDecimal grandTotal,
        BigDecimal amountReceived,
        BigDecimal pendingAmount
) {}