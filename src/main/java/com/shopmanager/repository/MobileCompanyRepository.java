package com.shopmanager.repository;

import com.shopmanager.entity.MobileCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileCompanyRepository extends JpaRepository<MobileCompany, Long> {
}