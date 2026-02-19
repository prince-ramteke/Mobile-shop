package com.shopmanager.service;

import com.shopmanager.dto.mobileSale.MobileSaleRequest;
import com.shopmanager.dto.mobileSale.MobileSaleResponse;
import com.shopmanager.entity.MobileSale;

import com.shopmanager.dto.CustomerLedgerRow;


import java.util.List;

public interface MobileSaleService {


    List<MobileSaleResponse> search(String txt);

    List<CustomerLedgerRow> getCustomerLedger(Long customerId);

    Long createSale(MobileSaleRequest request);

//    List<MobileSale> getAll();

//    MobileSale getById(Long id);

    List<MobileSaleResponse> getAll();

    MobileSaleResponse getById(Long id);


    Long updateSale(Long id, MobileSaleRequest request);

    List<MobileSale> getPendingSales();

    void recoverPayment(Long id, Double amount);

}