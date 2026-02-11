package com.shopmanager.controller.sale;

import com.shopmanager.dto.sale.SaleRequest;
import com.shopmanager.dto.sale.SaleResponse;
import com.shopmanager.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SaleRequest request) {
        try {
            System.out.println("Creating sale for customer: " + request.getCustomerId());
            SaleResponse response = saleService.createSale(request);
            System.out.println("Sale created successfully: " + response.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.err.println("Error creating sale: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Server error");
            error.put("message", e.getMessage() != null ? e.getMessage() : "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SaleRequest request) {
        try {
            SaleResponse response = saleService.updateSale(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Update failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            SaleResponse response = saleService.getSaleById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not found");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<Page<SaleResponse>> list(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) Boolean pending,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SaleResponse> sales = saleService.listSales(start, end, pending, page, size);
        return ResponseEntity.ok(sales);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            saleService.deleteSale(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Delete failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ✅ ADD: Invoice PDF endpoint
    @GetMapping("/{id}/invoice")
    public ResponseEntity<?> generateInvoice(@PathVariable Long id) {
        try {
            // This will be handled by PdfService directly in a real implementation
            // For now, return the sale details
            SaleResponse sale = saleService.getSaleById(id);
            return ResponseEntity.ok(sale);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invoice generation failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}