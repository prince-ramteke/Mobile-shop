package com.shopmanager.service.impl;

import com.shopmanager.dto.message.MessageLogRequest;
import com.shopmanager.dto.message.MessageLogResponse;
import com.shopmanager.entity.Customer;
import com.shopmanager.entity.MessageLog;
import com.shopmanager.exception.ResourceNotFoundException;
import com.shopmanager.mapper.MessageLogMapper;
import com.shopmanager.repository.CustomerRepository;
import com.shopmanager.repository.MessageLogRepository;
import com.shopmanager.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageLogRepository messageLogRepository;
    private final CustomerRepository customerRepository;
    private final MessageLogMapper mapper;

    @Override
    public MessageLogResponse create(MessageLogRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        MessageLog entity = mapper.toEntity(request);
        entity.setCustomer(customer);

        return mapper.toResponse(messageLogRepository.save(entity));
    }

    @Override
    public MessageLogResponse get(Long id) {
        MessageLog log = messageLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message log not found"));

        return mapper.toResponse(log);
    }

    @Override
    public Page<MessageLogResponse> search(String query, int page, int size) {
        Page<MessageLog> logs = messageLogRepository.search(query, PageRequest.of(page, size));

        return logs.map(mapper::toResponse);
    }

    @Override
    public void delete(Long id) {
        if (!messageLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("Message log not found");
        }
        messageLogRepository.deleteById(id);
    }
}