    package com.shopmanager.report.controller;

    import com.shopmanager.dto.report.RevenueTrendItem;
    import com.shopmanager.dto.report.SalesSummaryResponse;
    import com.shopmanager.report.dto.*;
    import com.shopmanager.report.service.ReportService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.format.annotation.DateTimeFormat;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.time.LocalDate;
    import java.util.List;

    /**
     * Reports Controller - Provides various business reports
     * including daily, monthly, GST, and revenue reports.
     */
    @RestController
    @RequestMapping("/api/reports")
    @RequiredArgsConstructor
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public class ReportsController {

        private final ReportService reportService;

        @GetMapping("/daily")
        public ResponseEntity<DailySalesReportDto> getDailyReport(
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
            return ResponseEntity.ok(reportService.getDailySales(date));
        }

        @GetMapping("/monthly")
        public ResponseEntity<MonthlySalesReportDto> getMonthlyReport(
                @RequestParam int year,
                @RequestParam int month) {
            return ResponseEntity.ok(reportService.getMonthlySales(year, month));
        }

        @GetMapping("/date-range")
        public ResponseEntity<DateRangeReportResponse> getDateRangeReport(
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
            return ResponseEntity.ok(reportService.generateDateRangeReport(startDate, endDate));
        }

        @GetMapping("/gst")
        public ResponseEntity<GstSummaryReportDto> getGstReport(
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
            return ResponseEntity.ok(reportService.getGstSummary(startDate, endDate));
        }

        @GetMapping("/sales-summary")
        public ResponseEntity<SalesSummaryResponse> getSalesSummary(
                @RequestParam(defaultValue = "week") String period) {
            return ResponseEntity.ok(reportService.getSalesSummary(period));
        }

        @GetMapping("/revenue-trend")
        public ResponseEntity<List<RevenueTrendItem>> getRevenueTrend(
                @RequestParam(defaultValue = "30") int days) {
            return ResponseEntity.ok(reportService.getRevenueTrend(days));
        }
    }