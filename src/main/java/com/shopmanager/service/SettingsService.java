package com.shopmanager.service;

import com.shopmanager.dto.settings.SettingsRequest;
import com.shopmanager.dto.settings.SettingsResponse;
import org.springframework.stereotype.Service;

@Service
public interface SettingsService {
    SettingsResponse getSettings();
    SettingsResponse updateSettings(SettingsRequest request);
    Object testWhatsAppConnection();
    Object triggerBackup();
}