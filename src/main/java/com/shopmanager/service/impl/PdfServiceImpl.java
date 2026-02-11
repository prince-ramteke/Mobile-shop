package com.shopmanager.service.impl;

import com.shopmanager.entity.RepairJob;
import com.shopmanager.entity.Sale;
import com.shopmanager.exception.ResourceNotFoundException;
import com.shopmanager.repository.RepairJobRepository;
import com.shopmanager.repository.SaleRepository;
import com.shopmanager.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final SaleRepository saleRepository;
    private final RepairJobRepository repairJobRepository;
    private final SpringTemplateEngine templateEngine;

    @Override
    @Transactional(readOnly = true)  // ADD THIS for lazy loading
    public byte[] generateSaleInvoicePdf(Long saleId) {

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        // Force initialization of lazy collections
        if (sale.getCustomer() != null) {
            sale.getCustomer().getName(); // Force load
        }
        if (sale.getItems() != null) {
            sale.getItems().size(); // Force load
        }

        Context ctx = new Context();

        // Shop info
        ctx.setVariable("shopName", "Saurabh Mobile Shop");
        ctx.setVariable("shopAddress", "Main Road, City, State");
        ctx.setVariable("shopPhone", "+91 9876543210");
        ctx.setVariable("gstNumber", "GSTIN: 12ABCDE3456F7Z8");

        // Sale info
        ctx.setVariable("invoiceNumber", sale.getInvoiceNumber());
        ctx.setVariable("date", sale.getSaleDate());
        ctx.setVariable("customer", sale.getCustomer());
        ctx.setVariable("items", sale.getItems());

        // Amounts
        ctx.setVariable("subTotal", sale.getSubTotal());
        ctx.setVariable("cgstAmount", sale.getCgstAmount());
        ctx.setVariable("sgstAmount", sale.getSgstAmount());
        ctx.setVariable("igstAmount", sale.getIgstAmount());
        ctx.setVariable("totalTax", sale.getTotalTax());
        ctx.setVariable("grandTotal", sale.getGrandTotal());
        ctx.setVariable("amountReceived", sale.getAmountReceived());
        ctx.setVariable("pendingAmount", sale.getPendingAmount());

        ctx.setVariable("paymentMode", sale.getPaymentMode());
        ctx.setVariable("notes", sale.getNotes());

        String html = templateEngine.process("pdf/sale_invoice", ctx);
        return renderPdf(html);
    }

    @Override
    @Transactional(readOnly = true)  // ADD THIS
    public byte[] generateRepairReceiptPdf(Long repairJobId) {

        RepairJob job = repairJobRepository.findById(repairJobId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair job not found"));

        // Force initialization
        if (job.getCustomer() != null) {
            job.getCustomer().getName();
        }

        Context ctx = new Context();
        ctx.setVariable("job", job);
        ctx.setVariable("shopName", "Saurabh Mobile Shop");
        ctx.setVariable("shopPhone", "+91 9876543210");

        String html = templateEngine.process("pdf/repair_receipt", ctx);
        return renderPdf(html);
    }

    private byte[] renderPdf(String html) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }
}