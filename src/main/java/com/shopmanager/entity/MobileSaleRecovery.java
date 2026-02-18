package com.shopmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "mobile_sale_recoveries")
@Getter
@Setter
public class MobileSaleRecovery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long saleId;

    private Double amount;

    private Double pendingAfter;

    private LocalDateTime createdAt = LocalDateTime.now();
}