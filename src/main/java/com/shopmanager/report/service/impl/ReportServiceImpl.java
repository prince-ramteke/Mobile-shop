package com.shopmanager.report.service.impl;

import com.shopmanager.dto.report.RevenueTrendItem;
import com.shopmanager.report.dto.DailySalesReportDto;
import com.shopmanager.report.dto.GstSummaryReportDto;
import com.shopmanager.report.dto.MonthlySalesReportDto;
import com.shopmanager.report.dto.DateRangeReportResponse;
import com.shopmanager.dto.report.SalesSummaryResponse;
import com.shopmanager.report.service.ReportService;
import com.shopmanager.repository.SaleItemRepository;
import com.shopmanager.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service("customReportService")
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;

    @Override
    public DailySalesReportDto getDailySales(LocalDate date) {
        BigDecimal totalSales = saleRepository.sumTodaySales(date);
        Long invoiceCount = saleRepository.countTodayInvoices(date);
        BigDecimal totalTax = saleRepository.sumTotalTaxByDate(date);

        BigDecimal cashSales = BigDecimal.ZERO;
        BigDecimal creditSales = totalSales != null ? totalSales : BigDecimal.ZERO;

        return DailySalesReportDto.builder()
                .date(date)
                .totalSales(totalSales != null ? totalSales : BigDecimal.ZERO)
                .invoiceCount(invoiceCount != null ? invoiceCount.intValue() : 0)
                .totalTax(totalTax != null ? totalTax : BigDecimal.ZERO)
                .cashSales(cashSales)
                .creditSales(creditSales)
                .build();
    }

    @Override
    public MonthlySalesReportDto getMonthlySales(int year, int month) {
        BigDecimal totalSales = saleRepository.sumMonthlySales(year, month);
        Long invoiceCount = saleRepository.countMonthlyInvoices(year, month);
        BigDecimal totalTax = saleRepository.sumMonthlyTax(year, month);

        YearMonth yearMonth = YearMonth.of(year, month);
        int totalDays = yearMonth.lengthOfMonth();

        return MonthlySalesReportDto.builder()
                .year(year)
                .month(month)
                .monthName(yearMonth.getMonth().toString())
                .totalSales(totalSales != null ? totalSales : BigDecimal.ZERO)
                .invoiceCount(invoiceCount != null ? invoiceCount.intValue() : 0)
                .totalTax(totalTax != null ? totalTax : BigDecimal.ZERO)
                .averageDailySales(totalSales != null && totalDays > 0
                        ? totalSales.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO)
                .build();
    }

    @Override
    public GstSummaryReportDto getGstSummary(LocalDate from, LocalDate to) {
        BigDecimal cgstTotal = saleRepository.sumCgstBetween(from, to);
        BigDecimal sgstTotal = saleRepository.sumSgstBetween(from, to);
        BigDecimal igstTotal = saleRepository.sumIgstBetween(from, to);
        BigDecimal totalTax = saleRepository.sumTaxBetween(from, to);
        BigDecimal totalSales = saleRepository.sumSalesBetween(from, to);
        Long invoiceCount = saleRepository.countSalesBetween(from, to);

        return GstSummaryReportDto.builder()
                .fromDate(from)
                .toDate(to)
                .cgstTotal(cgstTotal != null ? cgstTotal : BigDecimal.ZERO)
                .sgstTotal(sgstTotal != null ? sgstTotal : BigDecimal.ZERO)
                .igstTotal(igstTotal != null ? igstTotal : BigDecimal.ZERO)
                .totalTax(totalTax != null ? totalTax : BigDecimal.ZERO)
                .taxableAmount(totalSales != null ? totalSales.subtract(totalTax != null ? totalTax : BigDecimal.ZERO) : BigDecimal.ZERO)
                .totalInvoices(invoiceCount != null ? invoiceCount.intValue() : 0)
                .build();
    }

    public DateRangeReportResponse generateDateRangeReport(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalSales = saleRepository.sumSalesBetween(startDate, endDate);
        Long invoiceCount = saleRepository.countSalesBetween(startDate, endDate);
        BigDecimal totalTax = saleRepository.sumTaxBetween(startDate, endDate);

        BigDecimal subTotal = totalSales != null ? totalSales : BigDecimal.ZERO;
        BigDecimal tax = totalTax != null ? totalTax : BigDecimal.ZERO;
        BigDecimal grandTotal = subTotal.add(tax);

        BigDecimal amountReceived = BigDecimal.ZERO;
        BigDecimal pendingAmount = grandTotal.subtract(amountReceived);

        return DateRangeReportResponse.builder()
                .fromDate(startDate)
                .toDate(endDate)
                .totalInvoices(invoiceCount != null ? invoiceCount : 0L)
                .subTotal(subTotal)
                .totalTax(tax)
                .grandTotal(grandTotal)
                .amountReceived(amountReceived)
                .pendingAmount(pendingAmount)
                .build();
    }

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