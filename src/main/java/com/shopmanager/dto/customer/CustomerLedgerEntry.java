package com.shopmanager.dto.customer;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLedgerEntry {

    private LocalDateTime date;
    private String type;        // SALE / REPAIR / PAYMENT
    private String reference;   // Invoice / JobNumber
    private BigDecimal debit;   // Business amount (+)
    private BigDecimal credit;  // Payment (-)
    private BigDecimal balance; // Running balance
}