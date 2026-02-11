package com.shopmanager.audit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_user", columnList = "username")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who did it
    @Column(nullable = false)
    private String username;

    // What action
    @Column(nullable = false)
    private String action;

    // SALE / REPAIR / SETTINGS / DUE
    @Column(nullable = false)
    private String entityType;

    // saleId / repairId / etc
    private Long entityId;

    // Extra info (optional)
    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}