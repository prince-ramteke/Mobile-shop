package com.shopmanager.repository;

import com.shopmanager.entity.MobileSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileSaleRepository extends JpaRepository<MobileSale, Long> {
}