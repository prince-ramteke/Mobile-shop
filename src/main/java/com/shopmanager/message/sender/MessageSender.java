package com.shopmanager.message.sender;

import com.shopmanager.entity.enums.MessageChannel;
import com.shopmanager.message.dto.MessagePayload;
import com.shopmanager.message.dto.SendResult;

public interface MessageSender {

    MessageChannel supports();

    SendResult send(MessagePayload payload) throws Exception;
}