package com.shopmanager.service.impl;

import com.shopmanager.dto.due.DueCustomerDTO;
import com.shopmanager.entity.Customer;
import com.shopmanager.repository.CustomerRepository;
import com.shopmanager.repository.DuePaymentRepository;
import com.shopmanager.repository.SaleRepository;
import com.shopmanager.repository.RepairJobRepository;
import com.shopmanager.service.DueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DueServiceImpl implements DueService {
    private final DuePaymentRepository paymentRepository;

    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;
    private final RepairJobRepository repairJobRepository;

    @Override
    public List<DueCustomerDTO> getAllDues() {

        List<DueCustomerDTO> list = new ArrayList<>();

        for (Customer c : customerRepository.findAll()) {

            BigDecimal saleDue = saleRepository.sumPendingByCustomerId(c.getId());
            BigDecimal repairDue = repairJobRepository.sumPendingByCustomerId(c.getId());

            BigDecimal payments = paymentRepository.sumPaymentsByCustomerId(c.getId());
            if (payments == null) payments = BigDecimal.ZERO;

            BigDecimal totalDue = saleDue.add(repairDue).subtract(payments);

            if (totalDue.compareTo(BigDecimal.ZERO) < 0) {
                totalDue = BigDecimal.ZERO;
            }


            if (totalDue.compareTo(BigDecimal.ZERO) <= 0) continue;

            LocalDate lastSale = saleRepository.findLastSaleDate(c.getId());

            java.time.LocalDateTime lastRepairDT = repairJobRepository.findLastRepairDate(c.getId());
            LocalDate lastRepair = lastRepairDT != null ? lastRepairDT.toLocalDate() : null;

            LocalDate lastDate = lastSale;
            if (lastRepair != null && (lastDate == null || lastRepair.isAfter(lastDate))) {
                lastDate = lastRepair;
            }


            long overdue = 0;
            if (lastDate != null) {
                overdue = ChronoUnit.DAYS.between(lastDate, LocalDate.now());
            }

            list.add(
                    DueCustomerDTO.builder()
                            .customerId(c.getId())
                            .name(c.getName())
                            .phone(c.getPhone())
                            .totalPending(totalDue)
                            .lastTransactionDate(lastDate == null ? null : lastDate.atStartOfDay())
                            .overdueDays(overdue)
                            .build()
            );
        }

        return list;
    }
    @Override
    public void createDue(Long customerId,
                          com.shopmanager.entity.enums.DueReferenceType refType,
                          Long referenceId,
                          BigDecimal totalAmount,
                          BigDecimal paidAmount) {
        // SAFE stub (does nothing for now)
        // Required only to satisfy Sale + Repair module calls
    }

    @Override
    public void addPayment(com.shopmanager.entity.enums.DueReferenceType refType,
                           Long referenceId,
                           BigDecimal amount) {
        // SAFE stub (does nothing for now)
    }
    @Override
    public java.util.Optional<DueCustomerDTO> findByCustomerId(Long customerId) {
        return getAllDues()
                .stream()
                .filter(d -> d.getCustomerId().equals(customerId))
                .findFirst();
    }
    @Override
    public void save(DueCustomerDTO due) {
        // ⚠️ IMPORTANT
        // Due is calculated from Sale + Repair tables.
        // So we DO NOT store totalPending manually.
        // Payments already reduce Sale/Repair pending via business logic.

        // This method exists only to satisfy DueServiceExtended.
    }

}