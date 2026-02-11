package com.shopmanager.entity;

import com.shopmanager.entity.enums.DueReferenceType;
import com.shopmanager.entity.enums.DueStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "due_entries")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 CUSTOMER
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // 🔗 SALE / REPAIR
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DueReferenceType referenceType;

    @Column(nullable = false)
    private Long referenceId; // saleId or repairJobId

    // 💰 AMOUNTS
    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private BigDecimal paidAmount;

    @Column(nullable = false)
    private BigDecimal pendingAmount;

    // 📌 STATUS
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DueStatus status;

    // 🕒 AUDIT
    private LocalDateTime lastPaymentDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}