package com.shopmanager.report.service.impl;

import com.shopmanager.report.dto.DateRangeReportResponse;
import com.shopmanager.report.repository.DateRangeReportRepository;
import com.shopmanager.report.service.DateRangeReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DateRangeReportServiceImpl implements DateRangeReportService {

    private final DateRangeReportRepository reportRepository;

    @Override
    public DateRangeReportResponse getReport(LocalDate fromDate, LocalDate toDate) {

        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(23, 59, 59);

        Object[] row = reportRepository
                .getDateRangeSummary(start, end)
                .orElseThrow();

        return DateRangeReportResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalInvoices(row[0] != null ? (Long) row[0] : 0L)
                .subTotal(row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO)
                .totalTax(row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO)
                .grandTotal(row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO)
                .amountReceived(row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO)
                .pendingAmount(row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO)
                .build();
    }
}