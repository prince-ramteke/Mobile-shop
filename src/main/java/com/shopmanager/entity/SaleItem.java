package com.shopmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Sale sale;

    // ✅ MANUAL ITEM FIELDS
    private String itemName;     // back cover, service, repair
    private String type;         // Accessory / Service / Repair

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public void calculateLineTotal() {
        if (unitPrice == null || quantity == null) {
            this.lineTotal = BigDecimal.ZERO;
        } else {
            this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}