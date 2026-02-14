package com.shopmanager.service;

import com.shopmanager.entity.RepairJob;

public interface RepairInvoiceService {

    byte[] generateRepairInvoicePdf(RepairJob job);

}