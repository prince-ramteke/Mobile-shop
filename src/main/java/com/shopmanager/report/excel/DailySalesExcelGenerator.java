package com.shopmanager.report.excel;

import com.shopmanager.report.dto.DailySalesReportDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class DailySalesExcelGenerator {

    public byte[] generate(DailySalesReportDto dto) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Daily Sales");

            int rowIdx = 0;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Metric");
            header.createCell(1).setCellValue("Value");

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Date");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.getDate().toString());

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Total Invoices");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.getTotalInvoices());

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Total Sales");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.getTotalSales() != null ? dto.getTotalSales().doubleValue() : 0.0);

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Total Tax");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.getTotalTax() != null ? dto.getTotalTax().doubleValue() : 0.0);

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Amount Received");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.getTotalReceived().doubleValue());

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Pending Amount");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.getTotalPending().doubleValue());

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(out);
            return out.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to generate Daily Excel", e);
        }
    }
}