package com.shopmanager.message.sender.whatsapp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "whatsapp.api")
public class WhatsAppProperties {

    private String baseUrl;
    private String phoneNumberId;
    private String token;
}