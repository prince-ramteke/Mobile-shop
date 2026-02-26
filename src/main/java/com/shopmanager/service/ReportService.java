package com.shopmanager.service;

import com.shopmanager.dto.report.DailyReportDto;
import com.shopmanager.dto.report.DashboardSummaryDto;
import com.shopmanager.dto.report.MonthlyReportDto;

public interface ReportService {

    DashboardSummaryDto getDashboardSummary();

    DailyReportDto getDailyReport(String date);

    MonthlyReportDto getMonthlyReport(int year, int month);
}