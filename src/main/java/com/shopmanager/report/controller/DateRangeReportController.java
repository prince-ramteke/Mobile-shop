package com.shopmanager.report.controller;

import com.shopmanager.report.dto.DateRangeReportResponse;
import com.shopmanager.report.service.DateRangeReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DateRangeReportController {

    private final DateRangeReportService reportService;

    @GetMapping("/range")
    public DateRangeReportResponse getDateRangeReport(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return reportService.getReport(from, to);
    }
}