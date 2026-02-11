package com.shopmanager.report.repository;

import com.shopmanager.entity.Sale;
import com.shopmanager.report.dto.GstSummaryReportDto;
import com.shopmanager.report.dto.DailySalesReportDto;
import com.shopmanager.report.dto.MonthlySalesReportDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Sale, Long> {

    // ---------------- DAILY SALES ----------------
    @Query("""
        SELECT new com.shopmanager.report.dto.DailySalesReportDto(
            s.saleDate,
            COUNT(s.id),
            SUM(s.grandTotal),
            SUM(s.totalTax),
            SUM(s.amountReceived),
            SUM(s.pendingAmount)
        )
        FROM Sale s
        WHERE s.saleDate = :date
        GROUP BY s.saleDate
    """)
    DailySalesReportDto getDailySalesReport(LocalDate date);

    // ---------------- MONTHLY SALES ----------------
    @Query("""
        SELECT new com.shopmanager.report.dto.MonthlySalesReportDto(
            YEAR(s.saleDate),
            MONTH(s.saleDate),
            COUNT(s.id),
            SUM(s.grandTotal),
            SUM(s.totalTax)
        )
        FROM Sale s
        WHERE YEAR(s.saleDate) = :year AND MONTH(s.saleDate) = :month
        GROUP BY YEAR(s.saleDate), MONTH(s.saleDate)
    """)
    MonthlySalesReportDto getMonthlySalesReport(int year, int month);

    // ---------------- GST SUMMARY ----------------
    @Query("""
        SELECT new com.shopmanager.report.dto.GstSummaryReportDto(
            SUM(s.cgstAmount),
            SUM(s.sgstAmount),
            SUM(s.igstAmount),
            SUM(s.totalTax)
        )
        FROM Sale s
        WHERE s.saleDate BETWEEN :from AND :to
    """)
    GstSummaryReportDto getGstSummary(LocalDate from, LocalDate to);
}