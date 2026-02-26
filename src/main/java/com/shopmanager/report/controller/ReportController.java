package com.shopmanager.report.controller;

import com.shopmanager.dto.report.DailyReportDto;
import com.shopmanager.dto.report.DashboardSummaryDto;
import com.shopmanager.dto.report.MonthlyReportDto;
import com.shopmanager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Qualifier;

@RestController
@RequestMapping("/api/custom-reports")
@RequiredArgsConstructor
public class ReportController {

    @Qualifier("customReportService")
    private final ReportService reportService;


    @GetMapping("/dashboard")
    public DashboardSummaryDto getDashboard() {
        return reportService.getDashboardSummary();
    }

    @GetMapping("/daily")
    public DailyReportDto getDaily(@RequestParam String date) {
        return reportService.getDailyReport(date);
    }

    @GetMapping("/monthly")
    public MonthlyReportDto getMonthly(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return reportService.getMonthlyReport(year, month);
    }
}