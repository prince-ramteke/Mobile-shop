package com.shopmanager.service;

import com.shopmanager.dto.customer.CustomerLedgerEntry;
import java.util.List;

public interface CustomerLedgerService {
    List<CustomerLedgerEntry> getLedger(Long customerId);

    List<?> getSalePayments(String invoiceNumber);


}