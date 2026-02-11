package com.shopmanager.message.sender.impl;

import com.shopmanager.entity.enums.MessageChannel;
import com.shopmanager.message.dto.MessagePayload;
import com.shopmanager.message.dto.SendResult;
import com.shopmanager.message.sender.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class WhatsAppSender implements MessageSender {

    @Override
    public MessageChannel supports() {
        return MessageChannel.WHATSAPP;
    }

    @Override
    public SendResult send(MessagePayload payload) {

        log.info("📲 WhatsApp message sending");
        log.info("To: {}", payload.getRecipient());
        log.info("Message: {}", payload.getMessage());
        log.info("PDF attached: {}", payload.getPdf() != null);

        // simulate provider message id
        return new SendResult(UUID.randomUUID().toString());
    }
}