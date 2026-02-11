package com.shopmanager.report.excel;

import com.shopmanager.report.dto.MonthlyReportResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class MonthlySalesExcelGenerator {

    public byte[] generate(MonthlyReportResponse dto) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Monthly Sales");

            int rowIdx = 0;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Metric");
            header.createCell(1).setCellValue("Value");

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Month");
            sheet.getRow(rowIdx - 1).createCell(1)
                    .setCellValue(dto.year() + "-" + dto.month());

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Total Invoices");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.totalInvoices());

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Sub Total");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.subTotal().doubleValue());

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Total Tax");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.totalTax().doubleValue());

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Grand Total");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.grandTotal().doubleValue());

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Amount Received");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.amountReceived().doubleValue());

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Pending Amount");
            sheet.getRow(rowIdx - 1).createCell(1).setCellValue(dto.pendingAmount().doubleValue());

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(out);
            return out.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to generate Monthly Excel", e);
        }
    }
}