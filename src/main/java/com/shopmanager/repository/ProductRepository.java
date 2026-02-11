package com.shopmanager.repository;

import com.shopmanager.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(p.imei) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<Product> searchProducts(String query, Pageable pageable);
}