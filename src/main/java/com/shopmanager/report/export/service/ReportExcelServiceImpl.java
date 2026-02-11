package com.shopmanager.report.export.service;

import com.shopmanager.report.dto.DailySalesReportDto;
import com.shopmanager.report.dto.MonthlyReportResponse;
import com.shopmanager.report.excel.DailySalesExcelGenerator;
import com.shopmanager.report.excel.MonthlySalesExcelGenerator;
import com.shopmanager.report.service.MonthlyReportService;
import com.shopmanager.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReportExcelServiceImpl implements ReportExcelService {

    private final ReportService reportService;
    private final MonthlyReportService monthlyReportService;
    private final DailySalesExcelGenerator dailyGenerator;
    private final MonthlySalesExcelGenerator monthlyGenerator;

    @Override
    public byte[] generateDailySalesExcel(LocalDate date) {

        DailySalesReportDto report =
                reportService.getDailySales(date);

        return dailyGenerator.generate(report);
    }

    @Override
    public byte[] generateMonthlySalesExcel(int year, int month) {

        MonthlyReportResponse report =
                monthlyReportService.getMonthlyReport(year, month);

        return monthlyGenerator.generate(report);
    }
}