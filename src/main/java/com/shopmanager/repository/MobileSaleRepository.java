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


}