package com.shopmanager.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LedgerEntryDTO {

    private LocalDateTime date;
    private String type;
    private String reference;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal balance;
}