package com.shopmanager.repository;

import com.shopmanager.entity.InvoiceSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequence, Long> {

    Optional<InvoiceSequence> findByYear(Integer year);
}