package com.shopmanager.service.impl;

import com.shopmanager.entity.Sale;
import com.shopmanager.entity.enums.GstType;
import com.shopmanager.service.TaxCalculatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TaxCalculatorServiceImpl implements TaxCalculatorService {

    @Override
    public void calculateTaxes(Sale sale) {

        BigDecimal subTotal = sale.getSubTotal() != null
                ? sale.getSubTotal()
                : BigDecimal.ZERO;

        BigDecimal gstRate = sale.getGstRate() != null
                ? sale.getGstRate()
                : BigDecimal.ZERO;

        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal cgst = BigDecimal.ZERO;
        BigDecimal sgst = BigDecimal.ZERO;
        BigDecimal igst = BigDecimal.ZERO;

        if (gstRate.compareTo(BigDecimal.ZERO) > 0) {

            totalTax = subTotal
                    .multiply(gstRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (sale.getGstType() == GstType.CGST_SGST) {
                cgst = totalTax.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                sgst = totalTax.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            } else if (sale.getGstType() == GstType.IGST) {
                igst = totalTax;
            }
        }

        sale.setCgstAmount(cgst);
        sale.setSgstAmount(sgst);
        sale.setIgstAmount(igst);
        sale.setTotalTax(totalTax);
        sale.setGrandTotal(subTotal.add(totalTax));
    }
}