package com.shopmanager.repository;

import com.shopmanager.entity.RepairJob;
import com.shopmanager.entity.enums.RepairStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RepairJobRepository extends JpaRepository<RepairJob, Long> {

    Optional<RepairJob> findByJobNumber(String jobNumber);

    List<RepairJob> findByStatus(RepairStatus status);

    // Count methods for dashboard
    Long countByStatus(RepairStatus status);

    // For pending counts on dashboard
    Long countByPendingAmountGreaterThan(java.math.BigDecimal amount);

    @Query("""
SELECT r FROM RepairJob r
WHERE (:query IS NULL OR :query = '' 
       OR LOWER(r.customer.name) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(r.customer.phone) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(r.deviceBrand) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(r.deviceModel) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(r.imei) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(r.issueDescription) LIKE LOWER(CONCAT('%', :query, '%'))
)
ORDER BY r.id DESC
""")
    Page<RepairJob> search(@Param("query") String query, Pageable pageable);


    @Query("""
    SELECT r FROM RepairJob r
    WHERE r.pendingAmount > 0
""")
    List<RepairJob> findAllPendingRepairs();

    // For dashboard - count repairs created today
    @Query("SELECT COUNT(r) FROM RepairJob r WHERE DATE(r.createdAt) = :date")
    Long countByCreatedAtDate(@Param("date") LocalDate date);

    // For dashboard - count today's repairs (alternative)
    @Query("SELECT COUNT(r) FROM RepairJob r WHERE CAST(r.createdAt AS DATE) = :date")
    Long countTodayJobs(@Param("date") LocalDate date);


    @Query("SELECT r FROM RepairJob r LEFT JOIN FETCH r.customer WHERE r.id = :id")
    Optional<RepairJob> findByIdWithCustomer(@Param("id") Long id);

    // ================= CUSTOMER DUE =================
    @Query("""
SELECT COALESCE(SUM(r.pendingAmount),0)
FROM RepairJob r
WHERE r.customer.id = :customerId
""")
    BigDecimal sumPendingByCustomerId(@Param("customerId") Long customerId);


}