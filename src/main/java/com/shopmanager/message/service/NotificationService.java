package com.shopmanager.message.service;

import com.shopmanager.entity.MessageLog;
import com.shopmanager.entity.enums.MessageStatus;
import com.shopmanager.message.dto.MessagePayload;
import com.shopmanager.message.dto.SendResult;
import com.shopmanager.message.sender.MessageSender;
import com.shopmanager.repository.MessageLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final List<MessageSender> senders;
    private final MessageLogRepository messageLogRepository;

    public void send(MessageLog logEntity, MessagePayload payload) {

        MessageSender sender = senders.stream()
                .filter(s -> s.supports() == logEntity.getChannel())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No sender for " + logEntity.getChannel()));

        try {
            SendResult result = sender.send(payload);
            logEntity.setStatus(MessageStatus.SENT);
            logEntity.setProviderMessageId(result.providerMessageId());

        } catch (Exception e) {
            logEntity.setStatus(MessageStatus.FAILED);
            log.error("Message sending failed", e);
        }

        messageLogRepository.save(logEntity);
    }
}