package com.shopmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mobile_sales")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String model;

    private String imei1;
    private String imei2;

    private Integer quantity;

    private BigDecimal price;
    private BigDecimal totalAmount;

    private BigDecimal advancePaid;
    private BigDecimal pendingAmount;

    private Integer warrantyYears;
    private LocalDate warrantyExpiry;

    private Long customerId;

    private LocalDateTime createdAt;
}