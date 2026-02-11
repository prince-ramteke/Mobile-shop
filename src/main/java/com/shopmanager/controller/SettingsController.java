package com.shopmanager.controller;

import com.shopmanager.dto.settings.SettingsRequest;
import com.shopmanager.dto.settings.SettingsResponse;
import com.shopmanager.service.SettingsService;
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

    private final SettingsService settingsService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<SettingsResponse> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SettingsResponse> updateSettings(
            @Valid @RequestBody SettingsRequest request) {
        return ResponseEntity.ok(settingsService.updateSettings(request));
    }

    @GetMapping("/gst")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<SettingsResponse> getGstSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PutMapping("/gst")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SettingsResponse> updateGstSettings(
            @Valid @RequestBody SettingsRequest request) {
        return ResponseEntity.ok(settingsService.updateSettings(request));
    }

    @GetMapping("/whatsapp")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SettingsResponse> getWhatsAppSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PostMapping("/whatsapp/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testWhatsAppConnection() {
        return ResponseEntity.ok(settingsService.testWhatsAppConnection());
    }

    @PostMapping("/backup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> triggerBackup() {
        return ResponseEntity.ok(settingsService.triggerBackup());
    }
}