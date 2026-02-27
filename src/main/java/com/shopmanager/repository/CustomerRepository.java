package com.shopmanager.repository;

import com.shopmanager.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);

    boolean existsByPhone(String phone);

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Customer> searchCustomers(@Param("query") String query, Pageable pageable);

    // For listing all customers (when query is empty)
    @Query("SELECT c FROM Customer c ORDER BY c.createdAt DESC")
    Page<Customer> findAllOrderByCreatedAtDesc(Pageable pageable);

    List<Customer> findByIdIn(List<Long> ids);
}