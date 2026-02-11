package com.shopmanager.settings.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---- Shop Info ----
    @Column(nullable = false)
    private String shopName;

    private String shopPhone;
    private String shopAddress;
    private String gstNumber;

    // ---- Invoice ----
    private String invoiceFooter;

    // ---- Messaging Toggles ----
    @Column(nullable = false)
    private Boolean whatsappEnabled;

    @Column(nullable = false)
    private Boolean smsEnabled;

    @Column(nullable = false)
    private Boolean emailEnabled;

    // ---- Reminder ----
    @Column(nullable = false)
    private Integer reminderGapDays;
}