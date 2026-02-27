package com.shopmanager.report.controller;

import com.shopmanager.dto.report.AdvancedDashboardDto;
import com.shopmanager.dto.report.DailyReportDto;
import com.shopmanager.dto.report.DashboardSummaryDto;
import com.shopmanager.dto.report.MonthlyReportDto;
import com.shopmanager.service.ReportExcelService;
import com.shopmanager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@RestController
@RequestMapping("/api/custom-reports")
@RequiredArgsConstructor
public class ReportController {

    @Qualifier("customReportService")
    private final ReportService reportService;

    private final ReportExcelService reportExcelService;

    @GetMapping("/export/daily")
    public ResponseEntity<byte[]> exportDaily(@RequestParam String date) throws IOException {

        byte[] file = reportExcelService.exportDailyReport(date);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=daily-report.xlsx")
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(file);
    }

    @GetMapping("/export/monthly")
    public ResponseEntity<byte[]> exportMonthly(@RequestParam int year,
                                                @RequestParam int month) throws IOException {

        byte[] file = reportExcelService.exportMonthlyReport(year, month);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly-report.xlsx")
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(file);
    }
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

    @GetMapping("/advanced-dashboard")
    public AdvancedDashboardDto getAdvancedDashboard() {
        return reportService.getAdvancedDashboard();
    }
}