package com.shopmanager.dto.due;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DueCustomerDTO {

    private Long customerId;
    private String name;
    private String phone;

    private BigDecimal totalPending;
    private LocalDateTime lastTransactionDate;

    private long overdueDays;
}