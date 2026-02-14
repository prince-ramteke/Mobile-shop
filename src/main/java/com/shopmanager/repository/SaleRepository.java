package com.shopmanager.repository;

import com.shopmanager.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    // --------------------------------
    // SALES LISTING
    // --------------------------------
    @Query("""
    SELECT s FROM Sale s
    WHERE (:start IS NULL OR s.saleDate >= :start)
    AND   (:end IS NULL OR s.saleDate <= :end)
    AND   (:pendingOnly IS NULL OR :pendingOnly = false OR s.pendingAmount > 0)
""")
    Page<Sale> findSales(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("pendingOnly") Boolean pendingOnly,
            Pageable pageable
    );


    // --------------------------------
    // DASHBOARD QUERIES
    // --------------------------------

    @Query("""
        SELECT COALESCE(SUM(s.grandTotal), 0)
        FROM Sale s
        WHERE s.saleDate = :date
        """)
    BigDecimal sumTodaySales(@Param("date") LocalDate date);

    @Query("""
        SELECT COALESCE(SUM(s.grandTotal), 0)
        FROM Sale s
        WHERE YEAR(s.saleDate) = :year
        AND MONTH(s.saleDate) = :month
        """)
    BigDecimal sumMonthlySales(
            @Param("year") int year,
            @Param("month") int month
    );

    @Query("""
        SELECT COALESCE(SUM(s.pendingAmount), 0)
        FROM Sale s
        WHERE s.pendingAmount > 0
        """)
    BigDecimal sumPendingAmount();

    @Query("""
        SELECT COUNT(s)
        FROM Sale s
        WHERE s.saleDate = :date
        """)
    Long countTodayInvoices(@Param("date") LocalDate date);

    @Query("""
        SELECT COUNT(s)
        FROM Sale s
        WHERE YEAR(s.saleDate) = :year
        AND MONTH(s.saleDate) = :month
        """)
    Long countMonthlyInvoices(
            @Param("year") int year,
            @Param("month") int month
    );

    @Query("""
    SELECT s FROM Sale s
    WHERE s.pendingAmount > 0
""")
    List<Sale> findAllPendingSales();

    // --------------------------------
    // REPORT QUERIES (NEW)
    // --------------------------------

    @Query("SELECT COALESCE(SUM(s.totalTax), 0) FROM Sale s WHERE s.saleDate = :date")
    BigDecimal sumTotalTaxByDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(s.totalTax), 0) FROM Sale s WHERE YEAR(s.saleDate) = :year AND MONTH(s.saleDate) = :month")
    BigDecimal sumMonthlyTax(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COALESCE(SUM(s.grandTotal), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    BigDecimal sumSalesBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    Long countSalesBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(s.cgstAmount), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    BigDecimal sumCgstBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(s.sgstAmount), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    BigDecimal sumSgstBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(s.igstAmount), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    BigDecimal sumIgstBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(s.totalTax), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    BigDecimal sumTaxBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // For dashboard pending counts
    Long countByPendingAmountGreaterThan(BigDecimal amount);

    // ================= CUSTOMER DUE =================
@Query("""
    SELECT COALESCE(SUM(s.pendingAmount),0)
    FROM Sale s
    WHERE s.customer.id = :customerId
""")
BigDecimal sumPendingByCustomerId(@Param("customerId") Long customerId);

    // ================= CUSTOMER TOTAL BUSINESS =================
    @Query("""
    SELECT COALESCE(SUM(s.grandTotal),0)
    FROM Sale s
    WHERE s.customer.id = :customerId
""")
    BigDecimal sumTotalByCustomerId(@Param("customerId") Long customerId);


    // ================= CUSTOMER SALES LIST =================
    List<Sale> findByCustomerIdOrderBySaleDateDesc(Long customerId);


    @Query("""
SELECT s FROM Sale s
WHERE s.customer.id = :customerId
ORDER BY s.saleDate ASC, s.id ASC
""")
    List<Sale> findLedgerSales(@Param("customerId") Long customerId);


    // ================= CUSTOMER DASHBOARD =================

    Long countByCustomerId(Long customerId);

    @Query("""
SELECT MAX(s.saleDate)
FROM Sale s
WHERE s.customer.id = :customerId
""")
    LocalDate findLastSaleDate(@Param("customerId") Long customerId);


    Optional<Sale> findByInvoiceNumber(String invoiceNumber);

}