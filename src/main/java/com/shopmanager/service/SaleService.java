package com.shopmanager.service;

import com.shopmanager.dto.sale.SaleRequest;
import com.shopmanager.dto.sale.SaleResponse;
import org.springframework.data.domain.Page;

public interface SaleService {

    SaleResponse createSale(SaleRequest request);

    SaleResponse updateSale(Long id, SaleRequest request);

    SaleResponse getSaleById(Long id);

    Page<SaleResponse> listSales(String startDate, String endDate, Boolean pendingOnly, int page, int size);

    void deleteSale(Long id);
}