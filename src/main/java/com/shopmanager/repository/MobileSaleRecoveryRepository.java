package com.shopmanager.repository;

import com.shopmanager.entity.MobileSaleRecovery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MobileSaleRecoveryRepository extends JpaRepository<MobileSaleRecovery, Long> {

    List<MobileSaleRecovery> findBySaleIdOrderByCreatedAtDesc(Long saleId);
}