package com.shopmanager.dto.message;

import com.shopmanager.entity.enums.MessageChannel;
import com.shopmanager.entity.enums.MessageStatus;
import com.shopmanager.entity.enums.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageLogRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private MessageType type;

    @NotNull
    private MessageChannel channel;

    @NotNull
    private MessageStatus status;

    private String messageContent;

    private String providerMessageId;
}