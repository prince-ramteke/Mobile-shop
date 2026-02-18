package com.shopmanager.repository;

import com.shopmanager.entity.TemplateType;
import com.shopmanager.entity.WhatsAppTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WhatsAppTemplateRepository extends JpaRepository<WhatsAppTemplate, Long> {
    Optional<WhatsAppTemplate> findByTypeAndEnabledTrue(TemplateType type);
}