package com.shopmanager.dto.whatsapp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  // Optional - for frameworks that need default constructor
@AllArgsConstructor // Optional - ensures all-args constructor exists
public class WhatsAppMessageResponse {
    private boolean success;
    private String message;
    private String preview;
    private String status;
    private Long messageLogId;
}