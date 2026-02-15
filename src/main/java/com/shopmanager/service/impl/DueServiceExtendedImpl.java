package com.shopmanager.service.impl;

import com.shopmanager.dto.due.DueSummaryResponse;
import com.shopmanager.dto.due.MarkPaidRequest;
import com.shopmanager.entity.due.DuePayment;
import com.shopmanager.entity.enums.DueReferenceType;
import com.shopmanager.repository.DuePaymentRepository;
import com.shopmanager.service.DueService;
import com.shopmanager.service.DueServiceExtended;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class DueServiceExtendedImpl implements DueServiceExtended {

    private final DueService dueService;
    private final DuePaymentRepository paymentRepository;




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

        BigDecimal totalPending = BigDecimal.ZERO;
        long customersWithDue = 0;
        long overdueCustomers = 0;

        // Fetch all dues from main DueService logic
        var dues = dueService.getAllDues();

        for (var d : dues) {
            if (d.getTotalPending() != null) {
                totalPending = totalPending.add(d.getTotalPending());
            }

            customersWithDue++;

            if (d.getOverdueDays() > 7) {
                overdueCustomers++;
            }
        }

        return DueSummaryResponse.builder()
                .totalPending(totalPending)
                .customersWithDue(customersWithDue)
                .overdueCustomers(overdueCustomers)
                .build();
    }


    @Override
    public DueSummaryResponse markAsPaid(Long dueId, MarkPaidRequest request) {

        if (request == null || request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid payment amount");
        }

        var due = dueService.findByCustomerId(dueId)
                .orElseThrow(() -> new RuntimeException("Due not found"));

        BigDecimal payment = request.getAmount();

        // 1️⃣ Save payment entry
        DuePayment paymentEntry = new DuePayment();
        paymentEntry.setCustomerId(due.getCustomerId());
        paymentEntry.setAmount(payment);
        paymentEntry.setPaidAt(LocalDateTime.now());
        paymentEntry.setNote("Manual payment");

        paymentRepository.save(paymentEntry);

        // 2️⃣ Reduce pending
        BigDecimal newPending = due.getTotalPending().subtract(payment);

        if (newPending.compareTo(BigDecimal.ZERO) < 0) {
            newPending = BigDecimal.ZERO;
        }

        due.setTotalPending(newPending);

        // 3️⃣ Save updated due
        dueService.save(due);

        return DueSummaryResponse.builder()
                .customerId(due.getCustomerId())
                .customerName(due.getName())
                .totalPending(due.getTotalPending())
                .customersWithDue(newPending.compareTo(BigDecimal.ZERO) > 0 ? 1L : 0L)
                .overdueCustomers(due.getOverdueDays() > 7 ? 1L : 0L)
                .build();
    }





    @Override
    public void updateDueForRepair(Long repairId) {
        // TODO: Implement
    }

    @Override
    public String sendWhatsAppReminder(Long customerId) {

        var dueOpt = dueService.findByCustomerId(customerId);
        if (dueOpt.isEmpty()) {
            throw new RuntimeException("Customer due not found");
        }

        var due = dueOpt.get();

        String phone = due.getPhone();
        String name = due.getName();
        String amount = due.getTotalPending().toString();

        // 👉 WhatsApp message text
        String message = "Hello " + name +
                ", your pending amount is ₹" + amount +
                ". Please pay soon. Thank you!";

        // 👉 Call WhatsApp API (Replace with real provider later)
        System.out.println("Sending WhatsApp to " + phone + " : " + message);

        return "WhatsApp reminder sent to " + name;
    }

}