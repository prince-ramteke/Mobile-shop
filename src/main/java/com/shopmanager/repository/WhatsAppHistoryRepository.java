package com.shopmanager.repository;

import com.shopmanager.entity.WhatsAppHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WhatsAppHistoryRepository extends JpaRepository<WhatsAppHistory, Long> {
    List<WhatsAppHistory> findByPhone(String phone);
}