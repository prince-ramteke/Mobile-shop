package com.shopmanager.repository;

import com.shopmanager.entity.MobileSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MobileSaleRepository extends JpaRepository<MobileSale, Long> {

    List<MobileSale> findAllByOrderByIdDesc();

    List<MobileSale> findByPendingAmountGreaterThanOrderByCreatedAtDesc(BigDecimal amount);


}