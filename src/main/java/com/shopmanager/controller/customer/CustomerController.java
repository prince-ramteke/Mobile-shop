package com.shopmanager.controller.customer;

import com.shopmanager.repository.SaleRepository;
import com.shopmanager.repository.RepairJobRepository;
import com.shopmanager.entity.Customer;
import com.shopmanager.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.shopmanager.dto.customer.CustomerRequest;
import com.shopmanager.dto.customer.CustomerResponse;
import com.shopmanager.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final SaleRepository saleRepository;
    private final RepairJobRepository repairJobRepository;


    @PostMapping
    public ResponseEntity<CustomerResponse> create(@RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CustomerResponse>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(customerService.search(query, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable Long id,
            @RequestBody CustomerRequest request
    ) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    // ================= CUSTOMER FINANCIAL SUMMARY =================
    @GetMapping("/{id}/summary")
    public ResponseEntity<?> getCustomerSummary(@PathVariable Long id) {

        Customer customer = customerService.getEntityById(id);

        BigDecimal salePending = saleRepository.sumPendingByCustomerId(id);
        BigDecimal repairPending = repairJobRepository.sumPendingByCustomerId(id);

        if (salePending == null) salePending = BigDecimal.ZERO;
        if (repairPending == null) repairPending = BigDecimal.ZERO;

        BigDecimal totalDue = salePending.add(repairPending);

        Map<String, Object> res = new HashMap<>();
        res.put("salePending", salePending);
        res.put("repairPending", repairPending);
        res.put("totalDue", totalDue);

        return ResponseEntity.ok(res);
    }

}