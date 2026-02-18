package com.shopmanager.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerLedgerRow {

    private String type; // SALE or RECOVERY
    private Long saleId;

    private Double debit;   // Sale amount
    private Double credit;  // Recovery amount

    private Double balance; // Running pending

    private LocalDateTime date;
}