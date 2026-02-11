package com.shopmanager.dto.message;

import com.shopmanager.entity.enums.MessageChannel;
import com.shopmanager.entity.enums.MessageStatus;
import com.shopmanager.entity.enums.MessageType;
import lombok.Data;

@Data
public class MessageLogResponse {

    private Long id;
    private Long customerId;

    private MessageType type;
    private MessageChannel channel;

    private String messageContent;
    private String providerMessageId;

    private MessageStatus status;

    private String sentAt;
}