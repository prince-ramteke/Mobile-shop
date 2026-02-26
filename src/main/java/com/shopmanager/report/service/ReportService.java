package com.shopmanager.report.service;

import com.shopmanager.dto.report.DashboardSummaryDto;
import com.shopmanager.dto.report.RevenueTrendItem;
import com.shopmanager.dto.report.SalesSummaryResponse;
import com.shopmanager.report.dto.DailySalesReportDto;
import com.shopmanager.report.dto.GstSummaryReportDto;
import com.shopmanager.report.dto.MonthlySalesReportDto;

import java.time.LocalDate;

import com.shopmanager.report.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

//    DashboardSummaryDto getDashboardSummary();

    DailySalesReportDto getDailySales(LocalDate date);

    MonthlySalesReportDto getMonthlySales(int year, int month);

    GstSummaryReportDto getGstSummary(LocalDate from, LocalDate to);

    // Additional methods for ReportsController
    DateRangeReportResponse generateDateRangeReport(LocalDate startDate, LocalDate endDate);

    SalesSummaryResponse getSalesSummary(String period);

    List<RevenueTrendItem> getRevenueTrend(int days);
}