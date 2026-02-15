package com.shopmanager.controller.mobileSale;

import com.shopmanager.dto.mobileSale.MobileSaleRequest;
import com.shopmanager.service.MobileSaleService;
import com.shopmanager.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mobile-sales")
@RequiredArgsConstructor
public class MobileSaleController {

    private final PdfService pdfService;

    private final MobileSaleService saleService;

    @PostMapping
    public Long create(@RequestBody MobileSaleRequest request) {
        return saleService.createSale(request);
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {

        byte[] pdf = pdfService.generateMobileSaleInvoicePdf(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=MobileInvoice-" + id + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }

}