package com.shopmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopmanager.entity.enums.RepairStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "repair_jobs", indexes = {
        @Index(name = "idx_repair_job_number", columnList = "job_number"),
        @Index(name = "idx_repair_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_number", unique = true, nullable = false)
    private String jobNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    @Column(name = "device_brand", nullable = false)
    private String deviceBrand;

    @Column(name = "device_model", nullable = false)
    private String deviceModel;

    @Column
    private String imei;

    @Column(name = "issue_description", columnDefinition = "TEXT")
    private String issueDescription;

    @Column(name = "estimated_cost", precision = 14, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "final_cost", precision = 14, scale = 2)
    private BigDecimal finalCost;

    @Column(name = "advance_paid", precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal advancePaid = BigDecimal.ZERO;

    @Column(name = "pending_amount", precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal pendingAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RepairStatus status = RepairStatus.PENDING;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @PrePersist
    @PreUpdate
    private void preSave() {
        recalculatePending();
    }

    public void recalculatePending() {
        BigDecimal cost = (finalCost != null) ? finalCost : (estimatedCost != null ? estimatedCost : BigDecimal.ZERO);
        if (advancePaid == null) advancePaid = BigDecimal.ZERO;
        this.pendingAmount = cost.subtract(advancePaid);
        if (this.pendingAmount.compareTo(BigDecimal.ZERO) < 0) this.pendingAmount = BigDecimal.ZERO;
    }
}