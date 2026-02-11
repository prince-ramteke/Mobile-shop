package com.shopmanager.audit.service.impl;

import com.shopmanager.audit.entity.AuditLog;
import com.shopmanager.audit.repository.AuditLogRepository;
import com.shopmanager.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository repository;

    @Override
    public void log(String action, String entityType, Long entityId, String details) {

        String username = "SYSTEM";

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            username = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();
        }

        repository.save(
                AuditLog.builder()
                        .username(username)
                        .action(action)
                        .entityType(entityType)
                        .entityId(entityId)
                        .details(details)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }
}