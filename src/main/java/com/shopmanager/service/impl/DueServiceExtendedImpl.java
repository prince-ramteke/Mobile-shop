package com.shopmanager.service.impl;

import com.shopmanager.dto.due.DueSummaryResponse;
import com.shopmanager.dto.due.MarkPaidRequest;
import com.shopmanager.entity.enums.DueReferenceType;
import com.shopmanager.service.DueServiceExtended;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class DueServiceExtendedImpl implements DueServiceExtended {

    @Override
    public void createDue(Long customerId, DueReferenceType referenceType, Long referenceId,
                          BigDecimal totalAmount, BigDecimal paidAmount) {
        // TODO: Implement with DueRepository
    }

    @Override
    public void addPayment(DueReferenceType referenceType, Long referenceId, BigDecimal paymentAmount) {
        // TODO: Implement
    }

    @Override
    public Page<DueSummaryResponse> getAllDues(Pageable pageable) {
        // Return empty page for now
        return Page.empty(pageable);
    }

    @Override
    public Page<DueSummaryResponse> getOverdueDues(int days, Pageable pageable) {
        // Return empty page for now
        return Page.empty(pageable);
    }

    @Override
    public DueSummaryResponse getDueSummary() {
        // Return a sample/empty response using builder (valid approach)
        return DueSummaryResponse.builder()
                .type("SALE")
                .referenceId(0L)
                .referenceNumber("N/A")
                .customerId(0L)
                .customerName("No Data")
                .customerPhone("")
                .totalAmount(BigDecimal.ZERO)
                .pendingAmount(BigDecimal.ZERO)
                .date(LocalDate.now())
                .overdueDays(0L)
                .build();
    }

    @Override
    public DueSummaryResponse markAsPaid(Long dueId, MarkPaidRequest request) {
        // TODO: Implement actual logic
        return DueSummaryResponse.builder()
                .type("SALE")
                .referenceId(dueId)
                .referenceNumber("PAID")
                .customerId(0L)
                .customerName("Updated")
                .customerPhone("")
                .totalAmount(request.getAmount() != null ? request.getAmount() : BigDecimal.ZERO)
                .pendingAmount(BigDecimal.ZERO)
                .date(LocalDate.now())
                .overdueDays(0L)
                .build();
    }

    @Override
    public void updateDueForRepair(Long repairId) {
        // TODO: Implement
    }
}