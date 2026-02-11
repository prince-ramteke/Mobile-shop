package com.shopmanager.dto.settings;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettingsRequest {
    @NotBlank
    private String shopName;

    private String phone;
    private String address;
    private String gstNumber;

    @Min(0)
    @Max(100)
    private Integer gstPercentage;

    private String invoiceFooter;
    private Boolean whatsappEnabled;
    private Boolean autoReminders;

    @Min(1)
    @Max(30)
    private Integer reminderDays;
}