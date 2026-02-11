package com.shopmanager.dto.sale;

import com.shopmanager.entity.enums.PaymentMode;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SaleResponse {
    private Long id;
    private String invoiceNumber;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private LocalDate saleDate;
    private BigDecimal subTotal;
    private BigDecimal cgstAmount;
    private BigDecimal sgstAmount;
    private BigDecimal igstAmount;
    private BigDecimal totalTax;
    private BigDecimal grandTotal;
    private BigDecimal amountReceived;
    private BigDecimal pendingAmount;
    private PaymentMode paymentMode;
    private String notes;
    private String createdAt;

    // THIS FIELD MUST EXIST
    private List<SaleItemResponse> items;
}