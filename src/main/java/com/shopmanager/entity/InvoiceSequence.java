package com.shopmanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "invoice_sequences",
        uniqueConstraints = @UniqueConstraint(columnNames = {"year"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Long lastNumber;
}