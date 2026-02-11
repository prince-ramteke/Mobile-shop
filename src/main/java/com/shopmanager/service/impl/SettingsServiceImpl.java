package com.shopmanager.service.impl;

import com.shopmanager.dto.settings.SettingsRequest;
import com.shopmanager.dto.settings.SettingsResponse;
import com.shopmanager.service.SettingsService;
import com.shopmanager.settings.entity.ShopSettings;
import com.shopmanager.settings.repository.ShopSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SettingsServiceImpl implements SettingsService {

    private final ShopSettingsRepository repository;

    @Override
    public SettingsResponse getSettings() {
        ShopSettings settings = repository.findAll()
                .stream()
                .findFirst()
                .orElseGet(this::createDefault);

        return mapToResponse(settings);
    }

    @Override
    public SettingsResponse updateSettings(SettingsRequest req) {
        ShopSettings settings = repository.findAll()
                .stream()
                .findFirst()
                .orElseGet(this::createDefault);

        settings.setShopName(req.getShopName());
        settings.setShopPhone(req.getPhone());
        settings.setShopAddress(req.getAddress());
        settings.setGstNumber(req.getGstNumber());
        settings.setInvoiceFooter(req.getInvoiceFooter());
        settings.setWhatsappEnabled(req.getWhatsappEnabled());
        settings.setReminderGapDays(req.getReminderDays());

        ShopSettings saved = repository.save(settings);
        return mapToResponse(saved);
    }

    @Override
    public Object testWhatsAppConnection() {
        return "WhatsApp test placeholder";
    }

    @Override
    public Object triggerBackup() {
        return "Backup at " + LocalDateTime.now();
    }

    private ShopSettings createDefault() {
        ShopSettings s = new ShopSettings();
        s.setShopName("My Shop");
        s.setWhatsappEnabled(true);
        s.setReminderGapDays(3);
        return repository.save(s);
    }

    private SettingsResponse mapToResponse(ShopSettings s) {
        SettingsResponse r = new SettingsResponse();
        r.setShopName(s.getShopName());
        r.setPhone(s.getShopPhone());
        r.setAddress(s.getShopAddress());
        r.setGstNumber(s.getGstNumber());
        r.setGstPercentage(18);
        r.setInvoiceFooter(s.getInvoiceFooter());
        r.setWhatsappEnabled(s.getWhatsappEnabled());
        r.setReminderDays(s.getReminderGapDays());
        r.setAutoReminders(false);
        r.setLastBackup(LocalDateTime.now().toString());
        return r;
    }
}