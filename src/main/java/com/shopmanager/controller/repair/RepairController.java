package com.shopmanager.controller.repair;

import com.shopmanager.dto.repair.RepairJobRequest;
import com.shopmanager.dto.repair.RepairJobResponse;
import com.shopmanager.entity.Customer;
import com.shopmanager.entity.RepairJob;
import com.shopmanager.entity.enums.RepairStatus;
import com.shopmanager.exception.ResourceNotFoundException;
import com.shopmanager.mapper.RepairJobMapper;
import com.shopmanager.repository.CustomerRepository;
import com.shopmanager.repository.RepairJobRepository;
import com.shopmanager.service.InvoiceNumberService;
import com.shopmanager.service.RepairJobService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/repairs")
@RequiredArgsConstructor
public class RepairController {

    private final RepairJobService repairJobService;
    private final RepairJobRepository repairJobRepository;
    private final CustomerRepository customerRepository;
    private final RepairJobMapper repairJobMapper;
    private final InvoiceNumberService invoiceNumberService; // ADD THIS

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("pending", (long) repairJobRepository.findByStatus(RepairStatus.PENDING).size());
        stats.put("inProgress", (long) repairJobRepository.findByStatus(RepairStatus.IN_PROGRESS).size());
        stats.put("completed", (long) repairJobRepository.findByStatus(RepairStatus.COMPLETED).size());
        stats.put("delivered", (long) repairJobRepository.findByStatus(RepairStatus.DELIVERED).size());
        return ResponseEntity.ok(stats);
    }

    // 🔹 SEARCH MUST COME BEFORE {id}
    @GetMapping("/search")
    public ResponseEntity<Page<RepairJobResponse>> search(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<RepairJobResponse> repairs = repairJobService.searchRepairs(query, page, size);
        return ResponseEntity.ok(repairs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairJobResponse> get(@PathVariable Long id) {
        RepairJob job = repairJobRepository.findByIdWithCustomer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair job not found"));

        return ResponseEntity.ok(repairJobMapper.toResponse(job));
    }


    @PutMapping("/{id}/receive-payment")
    public ResponseEntity<?> receivePayment(
            @PathVariable Long id,
            @RequestParam("amount") String amountStr
    ) {
        try {
            // Convert safely
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid amount format"
                ));
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Amount must be greater than zero"
                ));
            }

            // Load job WITH customer
            RepairJob job = repairJobRepository.findByIdWithCustomer(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Repair job not found"));

            BigDecimal advance = job.getAdvancePaid() == null ? BigDecimal.ZERO : job.getAdvancePaid();
            BigDecimal finalCost = job.getFinalCost() == null ? BigDecimal.ZERO : job.getFinalCost();

            // Prevent overpayment
            if (advance.add(amount).compareTo(finalCost) > 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Payment exceeds total cost"
                ));
            }

            // Update advance
            job.setAdvancePaid(advance.add(amount));

            // Recalculate pending
            job.setPendingAmount(finalCost.subtract(job.getAdvancePaid()));

            // Auto mark delivered if fully paid
            if (job.getPendingAmount().compareTo(BigDecimal.ZERO) == 0) {
                job.setStatus(RepairStatus.DELIVERED);
                job.setDeliveredAt(java.time.LocalDateTime.now());
            }

            RepairJob saved = repairJobRepository.save(job);

            return ResponseEntity.ok(repairJobMapper.toResponse(saved));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Payment failed",
                    "message", e.getMessage()
            ));
        }
    }




    @GetMapping
    public ResponseEntity<List<RepairJobResponse>> list() {
        List<RepairJob> jobs = repairJobRepository.findAll();
        // Force load customers to avoid lazy loading issues
        jobs.forEach(job -> {
            if (job.getCustomer() != null) job.getCustomer().getName();
        });
        List<RepairJobResponse> responses = jobs.stream()
                .map(repairJobMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<RepairJobResponse> get(@PathVariable Long id) {
//        RepairJob job = repairJobRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Repair job not found"));
//        return ResponseEntity.ok(repairJobMapper.toResponse(job));
//    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RepairJobRequest request) {
        try {
            System.out.println("Received repair request: " + request);

            if (request.getCustomerId() == null) {
                throw new IllegalArgumentException("Customer ID is required");
            }

            // Fetch customer
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

            // Map DTO to Entity
            RepairJob job = repairJobMapper.toEntity(request);
            job.setCustomer(customer);

            // GENERATE JOB NUMBER - CRITICAL FIX!
            String jobNumber = invoiceNumberService.generateInvoiceNumber().replace("SMS", "JOB");
            job.setJobNumber(jobNumber);
            System.out.println("Generated job number: " + jobNumber);

            // Set defaults
            if (job.getStatus() == null) {
                job.setStatus(RepairStatus.PENDING);
            }
            if (job.getEstimatedCost() == null) {
                job.setEstimatedCost(BigDecimal.ZERO);
            }
            if (job.getFinalCost() == null) {
                job.setFinalCost(job.getEstimatedCost());
            }
            if (job.getAdvancePaid() == null) {
                job.setAdvancePaid(BigDecimal.ZERO);
            }

            // Calculate pending amount
            if (job.getFinalCost() != null && job.getAdvancePaid() != null) {
                job.setPendingAmount(job.getFinalCost().subtract(job.getAdvancePaid()));
            } else {
                job.setPendingAmount(BigDecimal.ZERO);
            }

            RepairJob saved = repairJobRepository.save(job);
            System.out.println("Repair job saved with ID: " + saved.getId());

            return ResponseEntity.ok(repairJobMapper.toResponse(saved));
        } catch (Exception e) {
            System.err.println("Error creating repair: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create repair");
            error.put("message", e.getMessage() != null ? e.getMessage() : "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody RepairJobRequest request)
 {
        try {
            // ⭐ LOAD WITH CUSTOMER (IMPORTANT)
            RepairJob existing = repairJobRepository.findByIdWithCustomer(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Repair job not found"));

            // ⭐ FETCH CUSTOMER SAFELY
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

            // ⭐ SET CUSTOMER (IMPORTANT)
            existing.setCustomer(customer);

            // Update fields
            existing.setDeviceBrand(request.getDeviceBrand());
            existing.setDeviceModel(request.getDeviceModel());
            existing.setImei(request.getImei());
            existing.setIssueDescription(request.getIssueDescription());

            existing.setEstimatedCost(
                    request.getEstimatedCost() != null ? request.getEstimatedCost() : existing.getEstimatedCost()
            );

            existing.setFinalCost(
                    request.getFinalCost() != null ? request.getFinalCost() : existing.getEstimatedCost()
            );

            existing.setAdvancePaid(
                    request.getAdvancePaid() != null ? request.getAdvancePaid() : existing.getAdvancePaid()
            );

            existing.setStatus(
                    request.getStatus() != null ? request.getStatus() : existing.getStatus()
            );

            // ⭐ Recalculate pending
            existing.recalculatePending();

            RepairJob saved = repairJobRepository.save(existing);

            return ResponseEntity.ok(repairJobMapper.toResponse(saved));

        } catch (Exception e) {
            e.printStackTrace();   // ⭐ VERY IMPORTANT FOR DEBUG
            Map<String, String> error = new HashMap<>();
            error.put("error", "Update failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repairJobRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}