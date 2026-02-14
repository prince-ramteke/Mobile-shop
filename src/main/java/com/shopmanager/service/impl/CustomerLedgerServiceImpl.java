package com.shopmanager.service.impl;

import com.shopmanager.dto.customer.CustomerLedgerEntry;
import com.shopmanager.entity.RepairJob;
import com.shopmanager.entity.Sale;
import com.shopmanager.repository.RepairJobRepository;
import com.shopmanager.repository.SaleRepository;
import com.shopmanager.service.CustomerLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomerLedgerServiceImpl implements CustomerLedgerService {

    private final SaleRepository saleRepository;
    private final RepairJobRepository repairJobRepository;

    @Override
    public List<CustomerLedgerEntry> getLedger(Long customerId) {

        List<CustomerLedgerEntry> ledger = new ArrayList<>();

        List<Sale> sales = saleRepository.findLedgerSales(customerId);
        List<RepairJob> repairs = repairJobRepository.findLedgerRepairs(customerId);

        for (Sale s : sales) {
            ledger.add(CustomerLedgerEntry.builder()
                    .date(s.getSaleDate().atStartOfDay())
                    .type("SALE")
                    .reference(s.getInvoiceNumber())
                    .debit(s.getGrandTotal())
                    .credit(BigDecimal.ZERO)
                    .build());
        }

        for (RepairJob r : repairs) {
            BigDecimal cost = r.getFinalCost() != null ? r.getFinalCost() : r.getEstimatedCost();

            ledger.add(CustomerLedgerEntry.builder()
                    .date(r.getCreatedAt())
                    .type("REPAIR")
                    .reference(r.getJobNumber())
                    .debit(cost == null ? BigDecimal.ZERO : cost)
                    .credit(r.getAdvancePaid())
                    .build());
        }

        ledger.sort(Comparator.comparing(CustomerLedgerEntry::getDate));

        BigDecimal running = BigDecimal.ZERO;
        for (CustomerLedgerEntry e : ledger) {
            running = running.add(e.getDebit()).subtract(e.getCredit());
            e.setBalance(running);
        }

        return ledger;
    }
}