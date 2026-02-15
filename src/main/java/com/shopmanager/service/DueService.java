package com.shopmanager.service;

import com.shopmanager.dto.due.DueCustomerDTO;
import java.util.List;
import java.util.Optional;

public interface DueService {
    List<DueCustomerDTO> getAllDues();

    void createDue(Long customerId,
                   com.shopmanager.entity.enums.DueReferenceType refType,
                   Long referenceId,
                   java.math.BigDecimal totalAmount,
                   java.math.BigDecimal paidAmount);

    void addPayment(com.shopmanager.entity.enums.DueReferenceType refType,
                    Long referenceId,
                    java.math.BigDecimal amount);

    Optional<DueCustomerDTO> findByCustomerId(Long customerId);
    void save(DueCustomerDTO due);

}