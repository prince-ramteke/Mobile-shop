package com.shopmanager.service.impl;

import com.shopmanager.dto.report.*;
import com.shopmanager.repository.SaleRepository;
import com.shopmanager.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportsServiceImpl implements ReportsService {

    private final SaleRepository saleRepository;

    @Override
    public DailyReportResponse generateDailyReport(LocalDate date) {
        BigDecimal totalSales = saleRepository.sumTodaySales(date);
        Long invoiceCount = saleRepository.countTodayInvoices(date);
        BigDecimal totalTax = saleRepository.sumTotalTaxByDate(date);

        return DailyReportResponse.builder()
                .date(date)
                .totalSales(totalSales != null ? totalSales : BigDecimal.ZERO)
                .invoiceCount(invoiceCount != null ? invoiceCount : 0L)
                .totalTax(totalTax != null ? totalTax : BigDecimal.ZERO)
                .build();
    }

    @Override
    public MonthlyReportResponse generateMonthlyReport(int year, int month) {
        BigDecimal totalSales = saleRepository.sumMonthlySales(year, month);
        Long invoiceCount = saleRepository.countMonthlyInvoices(year, month);
        BigDecimal totalTax = saleRepository.sumMonthlyTax(year, month);

        return MonthlyReportResponse.builder()
                .year(year)
                .month(month)
                .totalSales(totalSales != null ? totalSales : BigDecimal.ZERO)
                .invoiceCount(invoiceCount != null ? invoiceCount : 0L)
                .totalTax(totalTax != null ? totalTax : BigDecimal.ZERO)
                .build();
    }

    @Override
    public DateRangeReportResponse generateDateRangeReport(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalSales = saleRepository.sumSalesBetween(startDate, endDate);
        Long invoiceCount = saleRepository.countSalesBetween(startDate, endDate);
        BigDecimal totalTax = saleRepository.sumTaxBetween(startDate, endDate);

        return DateRangeReportResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalSales(totalSales != null ? totalSales : BigDecimal.ZERO)
                .invoiceCount(invoiceCount != null ? invoiceCount.intValue() : 0)  // Fixed: changed 0L to 0
                .totalTax(totalTax != null ? totalTax : BigDecimal.ZERO)
                .build();
    }

    @Override
    public GstReportResponse generateGstReport(LocalDate startDate, LocalDate endDate) {
        BigDecimal cgstTotal = saleRepository.sumCgstBetween(startDate, endDate);
        BigDecimal sgstTotal = saleRepository.sumSgstBetween(startDate, endDate);
        BigDecimal igstTotal = saleRepository.sumIgstBetween(startDate, endDate);

        return GstReportResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .cgstTotal(cgstTotal != null ? cgstTotal : BigDecimal.ZERO)
                .sgstTotal(sgstTotal != null ? sgstTotal : BigDecimal.ZERO)
                .igstTotal(igstTotal != null ? igstTotal : BigDecimal.ZERO)
                .totalTax(
                        (cgstTotal != null ? cgstTotal : BigDecimal.ZERO)
                                .add(sgstTotal != null ? sgstTotal : BigDecimal.ZERO)
                                .add(igstTotal != null ? igstTotal : BigDecimal.ZERO)
                )
                .build();
    }

    @Override
    public SalesSummaryResponse getSalesSummary(String period) {
        LocalDate end = LocalDate.now();
        LocalDate start;

        switch (period.toLowerCase()) {
            case "week":
                start = end.minusDays(6);
                break;
            case "month":
                start = end.minusDays(29);
                break;
            case "year":
                start = end.minusDays(364);
                break;
            default:
                start = end.minusDays(6);
        }

        BigDecimal totalSales = saleRepository.sumSalesBetween(start, end);
        Long invoiceCount = saleRepository.countSalesBetween(start, end);

        return SalesSummaryResponse.builder()
                .period(period)
                .totalSales(totalSales != null ? totalSales : BigDecimal.ZERO)
                .invoiceCount(invoiceCount != null ? invoiceCount : 0L)
                .build();
    }

    @Override
    public List<RevenueTrendItem> getRevenueTrend(int days) {
        List<RevenueTrendItem> trend = new ArrayList<>();
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days - 1);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            BigDecimal sales = saleRepository.sumTodaySales(date);
            trend.add(RevenueTrendItem.builder()
                    .date(date)
                    .amount(sales != null ? sales : BigDecimal.ZERO)
                    .build());
        }

        return trend;
    }
}