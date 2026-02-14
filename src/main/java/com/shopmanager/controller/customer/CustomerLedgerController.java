package com.shopmanager.controller.customer;

import com.shopmanager.dto.customer.CustomerLedgerEntry;
import com.shopmanager.service.CustomerLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerLedgerController {

    private final CustomerLedgerService ledgerService;

    // ================= LEDGER =================
    @GetMapping("/{customerId}/ledger")
    public List<CustomerLedgerEntry> getLedger(@PathVariable Long customerId) {
        return ledgerService.getLedger(customerId);
    }

    // ================= SALE PAYMENTS (Popup) =================
    @GetMapping("/ledger/SALE/{invoiceNumber}/payments")
    public List<?> getSalePayments(@PathVariable String invoiceNumber) {
        return ledgerService.getSalePayments(invoiceNumber);
    }
}