package com.shopmanager.dto.whatsapp;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WhatsAppTemplateResponse {
    private Long id;
    private String name;
    private String content;
    private String type;
    private boolean active;
}