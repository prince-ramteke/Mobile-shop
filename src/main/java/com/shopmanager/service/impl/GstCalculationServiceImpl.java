package com.shopmanager.service.impl;

import com.shopmanager.entity.Sale;
import com.shopmanager.entity.enums.GstType;
import com.shopmanager.service.GstCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GstCalculationServiceImpl implements GstCalculationService {

    @Override
    public void applyGst(Sale sale) {

        BigDecimal subTotal = sale.getSubTotal();
        BigDecimal rate = sale.getGstRate();

        if (sale.getGstType() == GstType.NONE || rate.compareTo(BigDecimal.ZERO) == 0) {
            sale.setCgstAmount(BigDecimal.ZERO);
            sale.setSgstAmount(BigDecimal.ZERO);
            sale.setIgstAmount(BigDecimal.ZERO);
            sale.setTotalTax(BigDecimal.ZERO);
            sale.setGrandTotal(subTotal);
            return;
        }

        BigDecimal gstAmount =
                subTotal.multiply(rate).divide(BigDecimal.valueOf(100));

        if (sale.getGstType() == GstType.CGST_SGST) {
            BigDecimal half = gstAmount.divide(BigDecimal.valueOf(2));
            sale.setCgstAmount(half);
            sale.setSgstAmount(half);
            sale.setIgstAmount(BigDecimal.ZERO);
        }

        if (sale.getGstType() == GstType.IGST) {
            sale.setIgstAmount(gstAmount);
            sale.setCgstAmount(BigDecimal.ZERO);
            sale.setSgstAmount(BigDecimal.ZERO);
        }

        sale.setTotalTax(gstAmount);
        sale.setGrandTotal(subTotal.add(gstAmount));
    }
}