package com.shopmanager.dto.due;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DueSummaryResponse {

    private String type;

    // Reference info (SALE / REPAIR)
    private Long referenceId;
    private String referenceNumber;

    // Customer info
    private Long customerId;
    private String customerName;
    private String customerPhone;

    // Amount info
    private BigDecimal totalAmount;     // ⭐ ADD
    private BigDecimal pendingAmount;   // ⭐ ADD

    // Date + overdue
    private LocalDate date;             // ⭐ ADD
    private Long overdueDays;           // ⭐ ADD

    // Dashboard summary (top cards)
    private BigDecimal totalPending;
    private Long customersWithDue;
    private Long overdueCustomers;
}