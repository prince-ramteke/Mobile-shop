package com.shopmanager.message.sender.whatsapp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WhatsAppApiClient {

    private final RestTemplate restTemplate;
    private final WhatsAppProperties properties;

    public String sendMessage(Map<String, Object> body) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(properties.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        properties.getBaseUrl() + "/" +
                                properties.getPhoneNumberId() + "/messages",
                        entity,
                        Map.class
                );

        return ((Map)((java.util.List)response.getBody()
                .get("messages")).get(0)).get("id").toString();
    }
}