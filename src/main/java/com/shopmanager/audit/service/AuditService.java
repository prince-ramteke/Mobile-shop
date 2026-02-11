package com.shopmanager.audit.service;

public interface AuditService {

    void log(String action, String entityType, Long entityId, String details);
}