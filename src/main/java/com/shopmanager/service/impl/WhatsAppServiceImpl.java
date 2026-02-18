package com.shopmanager.service.impl;

import com.shopmanager.dto.whatsapp.WhatsAppMessageResponse;
import com.shopmanager.dto.whatsapp.WhatsAppTemplateResponse;
import com.shopmanager.entity.TemplateType;
import com.shopmanager.entity.WhatsAppHistory;
import com.shopmanager.repository.WhatsAppHistoryRepository;
import com.shopmanager.repository.WhatsAppTemplateRepository;
import com.shopmanager.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WhatsAppServiceImpl implements WhatsAppService {
    private final WhatsAppTemplateRepository templateRepository;
    private final WhatsAppHistoryRepository historyRepository;


    @Override
    public WhatsAppMessageResponse sendInvoice(Long saleId) {

        System.out.println("📲 WhatsApp Invoice Triggered for Sale ID: " + saleId);

        // 1. Get Template
        String message = templateRepository
                .findByTypeAndEnabledTrue(TemplateType.INVOICE)
                .map(t -> t.getContent())
                .orElse("Thank you for your purchase. Invoice generated.");

        // 2. Replace variables (future dynamic)
        message = message.replace("{saleId}", String.valueOf(saleId));

        // 3. Save History (mock phone for now)
        historyRepository.save(
                WhatsAppHistory.builder()
                        .phone("UNKNOWN")
                        .type(TemplateType.INVOICE)
                        .message(message)
                        .success(true)
                        .sentAt(java.time.LocalDateTime.now())
                        .build()
        );

        return WhatsAppMessageResponse.builder()
                .success(true)
                .message("WhatsApp Invoice Logged (Mock)")
                .build();
    }



    @Override
    public WhatsAppMessageResponse sendRepairUpdate(Long repairId) {
        return WhatsAppMessageResponse.builder()
                .success(true)
                .message("Repair update WhatsApp placeholder")
                .build();
    }

    @Override
    public WhatsAppMessageResponse previewMessage(String type, Long id) {
        return WhatsAppMessageResponse.builder()
                .success(true)
                .message("Preview: " + type + " for ID " + id)
                .build();
    }

    @Override
    public WhatsAppMessageResponse sendDueReminder(Long customerId) {
        return WhatsAppMessageResponse.builder()
                .success(true)
                .message("Due reminder sent to customer " + customerId)
                .build();
    }

    @Override
    public List<WhatsAppTemplateResponse> getTemplates() {
        return Collections.emptyList();
    }

    @Override
    public WhatsAppTemplateResponse updateTemplate(Long templateId, WhatsAppTemplateResponse templateData) {
        return templateData;
    }

    @Override
    public List<?> getReminderHistory(Long customerId) {
        return Collections.emptyList();
    }
}