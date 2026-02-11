package com.shopmanager.service;

import com.shopmanager.dto.report.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface ReportsService {
    DailyReportResponse generateDailyReport(LocalDate date);
    MonthlyReportResponse generateMonthlyReport(int year, int month);
    DateRangeReportResponse generateDateRangeReport(LocalDate startDate, LocalDate endDate);
    GstReportResponse generateGstReport(LocalDate startDate, LocalDate endDate);
    SalesSummaryResponse getSalesSummary(String period);
    List<RevenueTrendItem> getRevenueTrend(int days);
}