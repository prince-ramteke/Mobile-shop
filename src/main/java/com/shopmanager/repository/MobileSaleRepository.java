package com.shopmanager.repository;

import com.shopmanager.entity.MobileSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface MobileSaleRepository extends JpaRepository<MobileSale, Long> {

    @Query("""
SELECT s FROM MobileSale s
JOIN Customer c ON s.customerId = c.id
WHERE
LOWER(c.name) LIKE LOWER(CONCAT('%', :txt, '%'))
OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :txt, '%'))
OR LOWER(s.imei1) LIKE LOWER(CONCAT('%', :txt, '%'))
OR LOWER(s.imei2) LIKE LOWER(CONCAT('%', :txt, '%'))
ORDER BY s.createdAt DESC
""")
    List<MobileSale> search(@Param("txt") String txt);

    List<MobileSale> findAllByOrderByIdDesc();

    List<MobileSale> findByPendingAmountGreaterThanOrderByCreatedAtDesc(BigDecimal amount);

    List<MobileSale> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT DATE(m.createdAt), SUM(m.totalAmount)
    FROM MobileSale m
    WHERE m.createdAt BETWEEN :start AND :end
    GROUP BY DATE(m.createdAt)
""")
    List<Object[]> getDailySalesBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    SELECT FUNCTION('DATE_FORMAT', m.createdAt, '%Y-%m'),
           SUM(m.totalAmount)
    FROM MobileSale m
    WHERE m.createdAt BETWEEN :start AND :end
    GROUP BY FUNCTION('DATE_FORMAT', m.createdAt, '%Y-%m')
""")
    List<Object[]> getMonthlySalesBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}