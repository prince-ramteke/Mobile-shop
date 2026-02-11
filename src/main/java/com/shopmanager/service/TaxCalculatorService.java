package com.shopmanager.service;

import com.shopmanager.entity.Sale;

public interface TaxCalculatorService {
    void calculateTaxes(Sale sale);
}