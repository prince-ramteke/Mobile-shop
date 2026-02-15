package com.shopmanager.dto.due;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkPaidRequest {

    private Long customerId;
    private BigDecimal amount;
    private String note;

}