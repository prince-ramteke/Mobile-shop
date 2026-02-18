package com.shopmanager.controller.mobileSale;

import com.shopmanager.dto.mobileSale.MobileSaleRequest;
import com.shopmanager.service.MobileSaleService;
import com.shopmanager.service.PdfService;
import com.shopmanager.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shopmanager.repository.MobileSaleRecoveryRepository;

@RestController
@RequestMapping("/api/mobile-sales")
@RequiredArgsConstructor
public class MobileSaleController {
    private final MobileSaleRecoveryRepository recoveryRepository;

    private final WhatsAppService whatsAppService;

    private final PdfService pdfService;

    private final MobileSaleService saleService;

    @PostMapping
    public Long create(@RequestBody MobileSaleRequest request) {
        return saleService.createSale(request);
    }

    // GET ALL MOBILE SALES
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(saleService.getAll());
    }


    // GET MOBILE SALE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getById(id));
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {

        byte[] pdf = pdfService.generateMobileSaleInvoicePdf(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=MobileInvoice-" + id + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MobileSaleRequest request) {
        return ResponseEntity.ok(saleService.updateSale(id, request));
    }
    // GET PENDING MOBILE SALES
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingSales() {
        return ResponseEntity.ok(saleService.getPendingSales());
    }
    // SEND PAYMENT REMINDER
    @PostMapping("/{id}/send-reminder")
    public ResponseEntity<?> sendReminder(@PathVariable Long id) {
        whatsAppService.sendInvoice(id);   // later replace with due-reminder template
        return ResponseEntity.ok("Reminder Sent");
    }

    // RECOVER PAYMENT (Reduce Pending)
    @PutMapping("/{id}/recover")
    public ResponseEntity<?> recoverPayment(
            @PathVariable Long id,
            @RequestParam Double amount) {

        saleService.recoverPayment(id, amount);
        return ResponseEntity.ok("Payment Recovered");
    }

    @GetMapping("/{id}/ledger")
    public ResponseEntity<?> getLedger(@PathVariable Long id) {
        return ResponseEntity.ok(
                recoveryRepository.findBySaleIdOrderByCreatedAtDesc(id)
        );
    }


}