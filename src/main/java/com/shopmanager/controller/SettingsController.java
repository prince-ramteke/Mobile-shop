package com.shopmanager.controller;

import com.shopmanager.settings.dto.ShopSettingsRequest;
import com.shopmanager.settings.dto.ShopSettingsResponse;
import com.shopmanager.settings.service.ShopSettingsService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Settings Controller - Manages shop configuration settings
 */
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final ShopSettingsService settingsService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ShopSettingsResponse> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopSettingsResponse> updateSettings(
            @Valid @RequestBody ShopSettingsRequest request) {
        return ResponseEntity.ok(settingsService.updateSettings(request));
    }
}