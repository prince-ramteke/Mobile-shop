package com.shopmanager.service;

import com.shopmanager.dto.mobileSale.MobileSaleRequest;

public interface MobileSaleService {

    Long createSale(MobileSaleRequest request);

}