package com.shopmanager.service;

public interface ReportPdfService {

    byte[] exportDailyPdf(String date);

    byte[] exportMonthlyPdf(int year, int month);
}