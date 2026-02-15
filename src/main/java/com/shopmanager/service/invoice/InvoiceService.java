package com.shopmanager.service.invoice;

import com.shopmanager.entity.MobileSale;

public interface InvoiceService {

    byte[] generateMobileSaleInvoice(MobileSale sale);

}