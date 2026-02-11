package com.shopmanager.repository;

import com.shopmanager.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByActiveTrue();

    List<Offer> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate start, LocalDate end);
}