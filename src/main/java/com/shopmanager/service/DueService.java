package com.shopmanager.service;

import com.shopmanager.entity.enums.DueReferenceType;

import java.math.BigDecimal;
import java.util.List;
import com.shopmanager.dto.due.DueSummaryResponse;
import org.springframework.data.domain.PageRequest;

public interface DueService {

    // 🔹 EXISTING
    List<DueSummaryResponse> getAllDues(PageRequest pageRequest);
    List<DueSummaryResponse> getOverdueDues(int days, PageRequest pageRequest);

    // 🔹 ADD THESE (already implemented in DueServiceImpl)
    void createDue(
            Long customerId,
            DueReferenceType referenceType,
            Long referenceId,
            BigDecimal totalAmount,
            BigDecimal paidAmount
    );

    void addPayment(
            DueReferenceType referenceType,
            Long referenceId,
            BigDecimal paymentAmount
    );
}