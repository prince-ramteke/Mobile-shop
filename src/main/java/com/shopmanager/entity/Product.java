package com.shopmanager.entity;

import com.shopmanager.entity.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String brand;

    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    private String imei;

    @Column(name = "selling_price", precision = 14, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "cost_price", precision = 14, scale = 2)
    private BigDecimal costPrice;

    private Integer stock;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "gst_applicable", nullable = false)
    @Builder.Default
    private Boolean gstApplicable = false;

    @Column(name = "gst_percent", precision = 5, scale = 2)
    private BigDecimal gstPercent;

}