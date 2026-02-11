package com.shopmanager.settings.repository;

import com.shopmanager.settings.entity.ShopSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopSettingsRepository extends JpaRepository<ShopSettings, Long> {
}