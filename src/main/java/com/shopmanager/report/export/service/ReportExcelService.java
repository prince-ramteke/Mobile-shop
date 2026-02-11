package com.shopmanager.report.export.service;

import java.time.LocalDate;

public interface ReportExcelService {

    byte[] generateDailySalesExcel(LocalDate date);

    byte[] generateMonthlySalesExcel(int year, int month);
}