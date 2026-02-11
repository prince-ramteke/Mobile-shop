package com.shopmanager.repository;

import com.shopmanager.entity.DueEntry;
import com.shopmanager.entity.enums.DueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DueEntryRepository extends JpaRepository<DueEntry, Long> {

    List<DueEntry> findByCustomerIdAndStatusIn(Long customerId, List<DueStatus> statuses);

    Optional<DueEntry> findByReferenceTypeAndReferenceId(
            com.shopmanager.entity.enums.DueReferenceType type,
            Long referenceId
    );

    List<DueEntry> findByStatus(DueStatus status);

    List<DueEntry> findByStatusAndLastPaymentDateBefore(
            DueStatus status,
            LocalDateTime date
    );

    List<DueEntry> findByStatusIn(List<DueStatus> statuses);

    // For pagination support
    Page<DueEntry> findByStatusIn(List<DueStatus> statuses, Pageable pageable);

    // For dashboard - sum of all pending amounts
    @Query("SELECT COALESCE(SUM(d.pendingAmount), 0) FROM DueEntry d WHERE d.status IN ('OPEN', 'PARTIALLY_PAID')")
    BigDecimal sumPendingAmount();

    // For due summary
    @Query("SELECT COUNT(d) FROM DueEntry d WHERE d.status IN ('OPEN', 'PARTIALLY_PAID')")
    Long countPendingDues();

    // For overdue count
    @Query("SELECT COUNT(d) FROM DueEntry d WHERE d.status IN ('OPEN', 'PARTIALLY_PAID') AND d.lastPaymentDate < :date")
    Long countOverdueDues(@Param("date") LocalDateTime date);

    // For overdue amount
    @Query("SELECT COALESCE(SUM(d.pendingAmount), 0) FROM DueEntry d WHERE d.status IN ('OPEN', 'PARTIALLY_PAID') AND d.lastPaymentDate < :date")
    BigDecimal sumOverdueAmount(@Param("date") LocalDateTime date);
}