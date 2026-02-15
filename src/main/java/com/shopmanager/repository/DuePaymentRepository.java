package com.shopmanager.repository;

import com.shopmanager.entity.due.DuePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DuePaymentRepository extends JpaRepository<DuePayment, Long> {
    @Query("SELECT COALESCE(SUM(p.amount),0) FROM DuePayment p WHERE p.customerId = :customerId")
    BigDecimal sumPaymentsByCustomerId(@Param("customerId") Long customerId);

    List<DuePayment> findByCustomerIdOrderByPaidAtDesc(Long customerId);
}