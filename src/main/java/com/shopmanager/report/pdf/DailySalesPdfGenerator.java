package com.shopmanager.report.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopmanager.report.dto.DailySalesReportDto;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class DailySalesPdfGenerator {

    public byte[] generate(DailySalesReportDto dto) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font labelFont = new Font(Font.HELVETICA, 12, Font.BOLD);

            document.add(new Paragraph("Daily Sales Report", titleFont));
            document.add(new Paragraph("Date: " + dto.getDate()));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            table.addCell("Total Invoices");
            table.addCell(String.valueOf(dto.getTotalInvoices()));

            table.addCell("Total Sales");
            table.addCell("₹ " + (dto.getTotalSales() != null ? dto.getTotalSales() : "0.00"));

            table.addCell("Total Tax");
            table.addCell("₹ " + (dto.getTotalTax() != null ? dto.getTotalTax() : "0.00"));

            table.addCell("Amount Received");
            table.addCell("₹ " + dto.getTotalReceived());

            table.addCell("Pending Amount");
            table.addCell("₹ " + dto.getTotalPending());

            document.add(table);
            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }

        return out.toByteArray();
    }
}