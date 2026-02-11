package com.shopmanager.report.service;

import com.shopmanager.report.dto.MonthlyReportResponse;

public interface MonthlyReportService {

    MonthlyReportResponse getMonthlyReport(int year, int month);
}