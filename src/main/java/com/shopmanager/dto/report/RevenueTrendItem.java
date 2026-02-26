package com.shopmanager.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class RevenueTrendItem {

    private LocalDate date;
    private BigDecimal amount;
}