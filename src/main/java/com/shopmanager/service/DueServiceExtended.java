package com.shopmanager.service;

import com.shopmanager.dto.due.DueSummaryResponse;
import com.shopmanager.dto.due.DueSummaryResponse;
import com.shopmanager.dto.due.MarkPaidRequest;
import com.shopmanager.entity.enums.DueReferenceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface DueServiceExtended {

    String sendWhatsAppReminder(Long customerId);

    // Existing methods
    void createDue(Long customerId, DueReferenceType referenceType, Long referenceId,
                   BigDecimal totalAmount, BigDecimal paidAmount);
    void addPayment(DueReferenceType referenceType, Long referenceId, BigDecimal paymentAmount);

    // New methods for controller
    Page<DueSummaryResponse> getAllDues(Pageable pageable);
    Page<DueSummaryResponse> getOverdueDues(int days, Pageable pageable);
    DueSummaryResponse getDueSummary();
    DueSummaryResponse markAsPaid(Long dueId, MarkPaidRequest request);
    void updateDueForRepair(Long repairId);
}