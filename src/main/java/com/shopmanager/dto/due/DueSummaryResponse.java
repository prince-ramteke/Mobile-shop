package com.shopmanager.dto.due;

import lombok.Builder;
import lombok.Data;

import com.shopmanager.entity.DueEntry;
import com.shopmanager.entity.enums.DueReferenceType;

import java.time.temporal.ChronoUnit;


import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class DueSummaryResponse {

    private String type; // SALE or REPAIR

    private Long referenceId; // saleId or repairJobId

    private String referenceNumber; // invoiceNumber / jobNumber

    private Long customerId;
    private String customerName;
    private String customerPhone;

    private BigDecimal totalAmount;
    private BigDecimal pendingAmount;

    private LocalDate date;
    private long overdueDays;
    public static DueSummaryResponse fromEntity(DueEntry due) {

        long overdueDays = 0;
        if (due.getLastPaymentDate() != null && due.getPendingAmount().compareTo(BigDecimal.ZERO) > 0) {
            overdueDays = ChronoUnit.DAYS.between(
                    due.getLastPaymentDate().toLocalDate(),
                    LocalDate.now()
            );
        }

        return DueSummaryResponse.builder()
                .type(due.getReferenceType().name()) // SALE / REPAIR
                .referenceId(due.getReferenceId())
                .referenceNumber(
                        due.getReferenceType() == DueReferenceType.SALE
                                ? "SALE-" + due.getReferenceId()
                                : "REPAIR-" + due.getReferenceId()
                )
                .customerId(due.getCustomer().getId())
                .customerName(due.getCustomer().getName())
                .customerPhone(due.getCustomer().getPhone())
                .totalAmount(due.getTotalAmount())
                .pendingAmount(due.getPendingAmount())
                .date(due.getLastPaymentDate() != null
                        ? due.getLastPaymentDate().toLocalDate()
                        : null)
                .overdueDays(overdueDays)
                .build();
    }

}