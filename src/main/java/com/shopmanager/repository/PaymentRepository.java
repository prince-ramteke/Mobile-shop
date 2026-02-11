package com.shopmanager.repository;

import com.shopmanager.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySaleId(Long saleId);

    List<Payment> findBySaleIdOrderByPaidAtDesc(Long saleId);
}