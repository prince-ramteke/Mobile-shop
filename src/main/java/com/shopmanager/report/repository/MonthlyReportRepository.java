package com.shopmanager.report.repository;

import com.shopmanager.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MonthlyReportRepository extends JpaRepository<Sale, Long> {

    @Query("""
        SELECT 
            COUNT(s.id),
            COALESCE(SUM(s.subTotal), 0),
            COALESCE(SUM(s.totalTax), 0),
            COALESCE(SUM(s.grandTotal), 0),
            COALESCE(SUM(s.amountReceived), 0),
            COALESCE(SUM(s.pendingAmount), 0)
        FROM Sale s
        WHERE s.saleDate BETWEEN :start AND :end
    """)
    Optional<Object[]> getMonthlySummary(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}