package com.shopmanager.report.export.service;

import com.shopmanager.report.dto.DailySalesReportDto;
import com.shopmanager.report.pdf.DailySalesPdfGenerator;
import com.shopmanager.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReportPdfServiceImpl implements ReportPdfService {

    private final ReportService reportService;
    private final DailySalesPdfGenerator pdfGenerator;

    @Override
    public byte[] generateDailySalesPdf(LocalDate date) {

        DailySalesReportDto report =
                reportService.getDailySales(date);

        return pdfGenerator.generate(report);
    }
}