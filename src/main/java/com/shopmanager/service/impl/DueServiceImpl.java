package com.shopmanager.service.impl;

import com.shopmanager.dto.due.DueSummaryResponse;
import com.shopmanager.entity.Customer;
import com.shopmanager.entity.DueEntry;
import com.shopmanager.entity.enums.DueReferenceType;
import com.shopmanager.entity.enums.DueStatus;
import com.shopmanager.exception.ResourceNotFoundException;
import com.shopmanager.repository.CustomerRepository;
import com.shopmanager.repository.DueEntryRepository;
import com.shopmanager.service.DueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DueServiceImpl implements DueService {

    private final DueEntryRepository dueEntryRepository;
    private final CustomerRepository customerRepository;

    // ================= EXISTING LOGIC (UNCHANGED) =================

    public void createDue(
            Long customerId,
            DueReferenceType referenceType,
            Long referenceId,
            BigDecimal totalAmount,
            BigDecimal paidAmount
    ) {
        if (totalAmount.compareTo(paidAmount) <= 0) return;

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        BigDecimal pending = totalAmount.subtract(paidAmount);

        DueEntry due = DueEntry.builder()
                .customer(customer)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .totalAmount(totalAmount)
                .paidAmount(paidAmount)
                .pendingAmount(pending)
                .status(
                        paidAmount.compareTo(BigDecimal.ZERO) == 0
                                ? DueStatus.OPEN
                                : DueStatus.PARTIALLY_PAID
                )
                .lastPaymentDate(LocalDateTime.now())
                .build();

        dueEntryRepository.save(due);
    }

    public void addPayment(
            DueReferenceType referenceType,
            Long referenceId,
            BigDecimal paymentAmount
    ) {
        DueEntry due = dueEntryRepository
                .findByReferenceTypeAndReferenceId(referenceType, referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Due entry not found"));

        BigDecimal newPaid = due.getPaidAmount().add(paymentAmount);
        BigDecimal newPending = due.getTotalAmount().subtract(newPaid);

        due.setPaidAmount(newPaid);
        due.setPendingAmount(newPending);
        due.setLastPaymentDate(LocalDateTime.now());

        if (newPending.compareTo(BigDecimal.ZERO) <= 0) {
            due.setStatus(DueStatus.CLEARED);
            due.setPendingAmount(BigDecimal.ZERO);
        } else {
            due.setStatus(DueStatus.PARTIALLY_PAID);
        }

        dueEntryRepository.save(due);
    }

    // ================= REQUIRED BY INTERFACE (NEW, SAFE) =================

    @Override
    public List<DueSummaryResponse> getAllDues(PageRequest pageRequest) {
        return dueEntryRepository.findAll()
                .stream()
                .map(DueSummaryResponse::fromEntity)
                .toList();
    }

    @Override
    public List<DueSummaryResponse> getOverdueDues(int days, PageRequest pageRequest) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);

        return dueEntryRepository
                .findByStatusAndLastPaymentDateBefore(DueStatus.OPEN, cutoff)
                .stream()
                .map(DueSummaryResponse::fromEntity)
                .toList();
    }

}