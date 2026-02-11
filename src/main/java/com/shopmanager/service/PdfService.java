package com.shopmanager.service;

public interface PdfService {

    byte[] generateSaleInvoicePdf(Long saleId);

    byte[] generateRepairReceiptPdf(Long repairJobId);
}