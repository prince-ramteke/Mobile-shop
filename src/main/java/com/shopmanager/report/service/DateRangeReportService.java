package com.shopmanager.report.service;

import com.shopmanager.report.dto.DateRangeReportResponse;

import java.time.LocalDate;

public interface DateRangeReportService {

    DateRangeReportResponse getReport(LocalDate fromDate, LocalDate toDate);
}