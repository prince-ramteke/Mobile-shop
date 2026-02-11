package com.shopmanager.report.export.service;

import java.time.LocalDate;

public interface ReportPdfService {

    byte[] generateDailySalesPdf(LocalDate date);
}