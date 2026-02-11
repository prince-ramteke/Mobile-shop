package com.shopmanager.settings.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShopSettingsResponse {

    private Long id;

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