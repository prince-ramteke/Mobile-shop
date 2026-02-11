package com.shopmanager.controller.payment;

import com.shopmanager.dto.payment.*;
import com.shopmanager.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> addPayment(
            @Valid @RequestBody PaymentRequest request
    ) {
        return ResponseEntity.ok(paymentService.addPayment(request));
    }

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<PaymentResponse>> getPayments(
            @PathVariable Long saleId
    ) {
        return ResponseEntity.ok(paymentService.getPaymentsBySale(saleId));
    }
}