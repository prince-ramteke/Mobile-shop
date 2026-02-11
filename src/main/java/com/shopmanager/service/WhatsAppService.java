package com.shopmanager.service;

import com.shopmanager.dto.whatsapp.WhatsAppMessageResponse;
import com.shopmanager.dto.whatsapp.WhatsAppTemplateResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WhatsAppService {
    WhatsAppMessageResponse sendInvoice(Long saleId);
    WhatsAppMessageResponse sendRepairUpdate(Long repairId);
    WhatsAppMessageResponse previewMessage(String type, Long id);
    WhatsAppMessageResponse sendDueReminder(Long customerId);
    List<WhatsAppTemplateResponse> getTemplates();
    WhatsAppTemplateResponse updateTemplate(Long templateId, WhatsAppTemplateResponse templateData);
    List<?> getReminderHistory(Long customerId);
}