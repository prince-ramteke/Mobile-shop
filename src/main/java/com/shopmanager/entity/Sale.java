package com.shopmanager.entity;

import com.shopmanager.entity.enums.GstType;
import com.shopmanager.entity.enums.PaymentMode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    private LocalDate saleDate;

    /* -------- BASIC -------- */
    @Builder.Default
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal amountReceived = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal pendingAmount = BigDecimal.ZERO;

    /* -------- GST -------- */
    private BigDecimal gstRate;

    @Enumerated(EnumType.STRING)
    private GstType gstType;

    @Builder.Default
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal igstAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalTax = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal grandTotal = BigDecimal.ZERO;

    /* -------- PAYMENT -------- */
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private String notes;

    /* -------- AUDIT -------- */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /* -------- RELATIONS -------- */
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SaleItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();
}