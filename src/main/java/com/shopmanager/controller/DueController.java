package com.shopmanager.controller;

import com.shopmanager.dto.due.DueSummaryResponse;
import com.shopmanager.dto.due.MarkPaidRequest;
import com.shopmanager.dto.whatsapp.WhatsAppMessageResponse;
import com.shopmanager.service.DueService;
import com.shopmanager.service.DueServiceExtended;
import com.shopmanager.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Due Controller - Manages pending dues and payment reminders
 */
@RestController
@RequestMapping("/api/dues")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DueController {

    private final DueServiceExtended dueService;
    private final WhatsAppService whatsAppService;

    @GetMapping
    public ResponseEntity<Page<DueSummaryResponse>> getAllDues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok((Page<DueSummaryResponse>) dueService.getAllDues(PageRequest.of(page, size)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<Page<DueSummaryResponse>> getOverdueDues(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok((Page<DueSummaryResponse>) dueService.getOverdueDues(days, PageRequest.of(page, size)));
    }

    @GetMapping("/summary")
    public ResponseEntity<DueSummaryResponse> getDueSummary() {
        return ResponseEntity.ok(dueService.getDueSummary());
    }

    @PostMapping("/{customerId}/send-reminder")
    public ResponseEntity<WhatsAppMessageResponse> sendReminderToCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(whatsAppService.sendDueReminder(customerId));
    }

    @PostMapping("/{dueId}/mark-paid")
    public ResponseEntity<DueSummaryResponse> markAsPaid(
            @PathVariable Long dueId,
            @Valid @RequestBody MarkPaidRequest request) {
        return ResponseEntity.ok(dueService.markAsPaid(dueId, request));
    }

    @GetMapping("/{customerId}/reminder-history")
    public ResponseEntity<?> getReminderHistory(@PathVariable Long customerId) {
        return ResponseEntity.ok(whatsAppService.getReminderHistory(customerId));
    }
}