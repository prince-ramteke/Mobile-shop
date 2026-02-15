package com.shopmanager.controller.due;

import com.shopmanager.dto.due.DueCustomerDTO;
import com.shopmanager.dto.due.DueSummaryResponse;
import com.shopmanager.dto.due.MarkPaidRequest;
import com.shopmanager.service.DueService;
import com.shopmanager.service.DueServiceExtended;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dues")
@RequiredArgsConstructor
public class DueController {

    private final DueServiceExtended dueServiceExtended;
    private final DueService dueService;

    // GET ALL DUES
    @GetMapping
    public List<DueCustomerDTO> getAllDues() {
        return dueService.getAllDues();
    }

    @GetMapping("/summary")
    public java.util.Map<String, Object> getSummary() {

        java.util.List<com.shopmanager.dto.due.DueCustomerDTO> dues = dueService.getAllDues();

        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        long overdue = 0;

        for (var d : dues) {
            total = total.add(d.getTotalPending());
            if (d.getOverdueDays() > 7) overdue++;
        }

        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("totalDue", total);
        map.put("customerCount", dues.size());
        map.put("overdueCount", overdue);

        return map;
    }
    @PostMapping("/{dueId}/mark-paid")
    public DueSummaryResponse markPaid(@PathVariable Long dueId, @RequestBody MarkPaidRequest req)
    {
        return dueServiceExtended.markAsPaid(dueId, req);
    }

}