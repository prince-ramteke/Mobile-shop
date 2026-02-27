package com.shopmanager.service;

import com.shopmanager.dto.report.DailyReportDto;
import com.shopmanager.dto.report.MonthlyReportDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ReportExcelService {

    private final ReportService reportService;

    // ================= DAILY EXPORT =================

    public byte[] exportDailyReport(String date) throws IOException {

        DailyReportDto dto = reportService.getDailyReport(date);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Daily Report");

        int rowNum = 0;

        // Title
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue("Daily Report - " + dto.getDate());

        rowNum++;

        // Summary
        Row summary1 = sheet.createRow(rowNum++);
        summary1.createCell(0).setCellValue("Total Revenue");
        summary1.createCell(1).setCellValue(dto.getTotalRevenue());

        Row summary2 = sheet.createRow(rowNum++);
        summary2.createCell(0).setCellValue("Total Sales");
        summary2.createCell(1).setCellValue(dto.getTotalSales());

        Row summary3 = sheet.createRow(rowNum++);
        summary3.createCell(0).setCellValue("Total Repairs");
        summary3.createCell(1).setCellValue(dto.getTotalRepairs());

        rowNum++;

        // Hourly Header
        Row hourlyHeader = sheet.createRow(rowNum++);
        hourlyHeader.createCell(0).setCellValue("Hour");
        hourlyHeader.createCell(1).setCellValue("Sales");
        hourlyHeader.createCell(2).setCellValue("Repairs");

        for (DailyReportDto.HourlyData h : dto.getHourlyData()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(h.getHour());
            row.createCell(1).setCellValue(h.getSales());
            row.createCell(2).setCellValue(h.getRepairs());
        }

        rowNum++;

        // Transactions Header
        Row txHeader = sheet.createRow(rowNum++);
        txHeader.createCell(0).setCellValue("ID");
        txHeader.createCell(1).setCellValue("Type");
        txHeader.createCell(2).setCellValue("Customer");
        txHeader.createCell(3).setCellValue("Amount");
        txHeader.createCell(4).setCellValue("Time");

        for (DailyReportDto.TransactionRow t : dto.getTransactions()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(t.getId());
            row.createCell(1).setCellValue(t.getType());
            row.createCell(2).setCellValue(t.getCustomer());
            row.createCell(3).setCellValue(t.getAmount());
            row.createCell(4).setCellValue(t.getTime());
        }

        autoSize(sheet, 5);

        return writeToByteArray(workbook);
    }

    // ================= MONTHLY EXPORT =================

    public byte[] exportMonthlyReport(int year, int month) throws IOException {

        MonthlyReportDto dto = reportService.getMonthlyReport(year, month);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Monthly Report");

        int rowNum = 0;

        Row titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue("Monthly Report - " + dto.getMonth());

        rowNum++;

        // Summary
        Row s1 = sheet.createRow(rowNum++);
        s1.createCell(0).setCellValue("Total Revenue");
        s1.createCell(1).setCellValue(dto.getTotalRevenue());

        Row s2 = sheet.createRow(rowNum++);
        s2.createCell(0).setCellValue("Total Sales");
        s2.createCell(1).setCellValue(dto.getTotalSales());

        Row s3 = sheet.createRow(rowNum++);
        s3.createCell(0).setCellValue("Total Repairs");
        s3.createCell(1).setCellValue(dto.getTotalRepairs());

        Row s4 = sheet.createRow(rowNum++);
        s4.createCell(0).setCellValue("Growth %");
        s4.createCell(1).setCellValue(dto.getGrowth());

        rowNum++;

        // Daily Data Header
        Row header = sheet.createRow(rowNum++);
        header.createCell(0).setCellValue("Day");
        header.createCell(1).setCellValue("Sales");
        header.createCell(2).setCellValue("Repairs");

        for (MonthlyReportDto.DailyData d : dto.getDailyData()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getDay());
            row.createCell(1).setCellValue(d.getSales());
            row.createCell(2).setCellValue(d.getRepairs());
        }

        rowNum++;

        // Top Customers Header
        Row tcHeader = sheet.createRow(rowNum++);
        tcHeader.createCell(0).setCellValue("Customer ID");
        tcHeader.createCell(1).setCellValue("Name");
        tcHeader.createCell(2).setCellValue("Total Spent");

        for (MonthlyReportDto.TopCustomer tc : dto.getTopCustomers()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(tc.getCustomerId());
            row.createCell(1).setCellValue(tc.getName());
            row.createCell(2).setCellValue(tc.getTotalSpent());
        }

        autoSize(sheet, 3);

        return writeToByteArray(workbook);
    }

    // ================= HELPERS =================

    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private byte[] writeToByteArray(Workbook workbook) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}