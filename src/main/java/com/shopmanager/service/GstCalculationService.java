package com.shopmanager.service;

import com.shopmanager.entity.Sale;

public interface GstCalculationService {
    void applyGst(Sale sale);
}