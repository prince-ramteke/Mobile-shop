package com.shopmanager.settings.dto;

import lombok.Data;

@Data
public class ShopSettingsRequest {

    private String shopName;
    private String shopPhone;
    private String shopAddress;
    private String gstNumber;
    private String invoiceFooter;

    private Boolean whatsappEnabled;
    private Boolean smsEnabled;
    private Boolean emailEnabled;

    private Integer reminderGapDays;
}