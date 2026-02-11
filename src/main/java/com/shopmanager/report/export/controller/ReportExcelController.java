package com.shopmanager.report.export.controller;

import com.shopmanager.report.export.service.ReportExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportExcelController {

    private final ReportExcelService reportExcelService;

    // ---------------- DAILY EXCEL ----------------
    @GetMapping("/daily/excel")
    public ResponseEntity<byte[]> exportDailyExcel(
            @RequestParam LocalDate date
    ) {

        byte[] excel = reportExcelService.generateDailySalesExcel(date);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=daily-sales-" + date + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    // ---------------- MONTHLY EXCEL ----------------
    @GetMapping("/monthly/excel")
    public ResponseEntity<byte[]> exportMonthlyExcel(
            @RequestParam int year,
            @RequestParam int month
    ) {

        byte[] excel = reportExcelService.generateMonthlySalesExcel(year, month);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=monthly-sales-" + year + "-" + month + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }
}