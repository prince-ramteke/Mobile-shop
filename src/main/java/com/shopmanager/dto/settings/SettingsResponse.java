package com.shopmanager.dto.settings;

import lombok.Data;

@Data
public class SettingsResponse {
    private String shopName;
    private String phone;
    private String address;
    private String gstNumber;
    private Integer gstPercentage;
    private String invoiceFooter;
    private Boolean whatsappEnabled;
    private Boolean autoReminders;
    private Integer reminderDays;
    private String lastBackup;
}