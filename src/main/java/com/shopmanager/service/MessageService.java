package com.shopmanager.service;

import com.shopmanager.dto.message.MessageLogRequest;
import com.shopmanager.dto.message.MessageLogResponse;
import org.springframework.data.domain.Page;

public interface MessageService {

    MessageLogResponse create(MessageLogRequest request);

    MessageLogResponse get(Long id);

    Page<MessageLogResponse> search(String query, int page, int size);

    void delete(Long id);
}