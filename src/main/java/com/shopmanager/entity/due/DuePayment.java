package com.shopmanager.entity.due;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "due_payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DuePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private BigDecimal amount;

    private String note;

    private LocalDateTime paidAt;
}