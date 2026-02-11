package com.shopmanager.settings.service.impl;

import com.shopmanager.settings.dto.ShopSettingsRequest;
import com.shopmanager.settings.dto.ShopSettingsResponse;
import com.shopmanager.settings.entity.ShopSettings;
import com.shopmanager.settings.repository.ShopSettingsRepository;
import com.shopmanager.settings.service.ShopSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopSettingsServiceImpl implements ShopSettingsService {

    private final ShopSettingsRepository repository;

    @Override
    public ShopSettingsResponse getSettings() {

        ShopSettings settings = repository.findAll()
                .stream()
                .findFirst()
                .orElseGet(this::createDefault);

        return toResponse(settings);
    }

    @Override
    public ShopSettingsResponse updateSettings(ShopSettingsRequest req) {

        ShopSettings settings = repository.findAll()
                .stream()
                .findFirst()
                .orElseGet(this::createDefault);

        settings.setShopName(req.getShopName());
        settings.setShopPhone(req.getShopPhone());
        settings.setShopAddress(req.getShopAddress());
        settings.setGstNumber(req.getGstNumber());
        settings.setInvoiceFooter(req.getInvoiceFooter());

        settings.setWhatsappEnabled(req.getWhatsappEnabled());
        settings.setSmsEnabled(req.getSmsEnabled());
        settings.setEmailEnabled(req.getEmailEnabled());
        settings.setReminderGapDays(req.getReminderGapDays());

        ShopSettings saved = repository.save(settings);

        return toResponse(saved);
    }

    // ---------------- PRIVATE ----------------

    private ShopSettings createDefault() {
        return repository.save(
                ShopSettings.builder()
                        .shopName("My Mobile Shop")
                        .whatsappEnabled(true)
                        .smsEnabled(false)
                        .emailEnabled(false)
                        .reminderGapDays(3)
                        .build()
        );
    }

    private ShopSettingsResponse toResponse(ShopSettings s) {
        return ShopSettingsResponse.builder()
                .id(s.getId())
                .shopName(s.getShopName())
                .shopPhone(s.getShopPhone())
                .shopAddress(s.getShopAddress())
                .gstNumber(s.getGstNumber())
                .invoiceFooter(s.getInvoiceFooter())
                .whatsappEnabled(s.getWhatsappEnabled())
                .smsEnabled(s.getSmsEnabled())
                .emailEnabled(s.getEmailEnabled())
                .reminderGapDays(s.getReminderGapDays())
                .build();
    }
}