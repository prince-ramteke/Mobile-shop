package com.shopmanager.settings.service;

import com.shopmanager.settings.dto.ShopSettingsRequest;
import com.shopmanager.settings.dto.ShopSettingsResponse;

public interface ShopSettingsService {

    ShopSettingsResponse getSettings();

    ShopSettingsResponse updateSettings(ShopSettingsRequest request);
}