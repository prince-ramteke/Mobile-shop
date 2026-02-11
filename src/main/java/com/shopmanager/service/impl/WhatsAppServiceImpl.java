package com.shopmanager.service.impl;

import com.shopmanager.dto.whatsapp.WhatsAppMessageResponse;
import com.shopmanager.dto.whatsapp.WhatsAppTemplateResponse;
import com.shopmanager.service.WhatsAppService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    @Override
    public WhatsAppMessageResponse sendInvoice(Long saleId) {
        return WhatsAppMessageResponse.builder()
                .success(true)
                .message("Invoice WhatsApp placeholder")
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