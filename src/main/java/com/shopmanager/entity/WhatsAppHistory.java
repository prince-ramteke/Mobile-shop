package com.shopmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phone;

    @Enumerated(EnumType.STRING)
    private TemplateType type;

    @Column(length = 2000)
    private String message;

    private boolean success;

    private LocalDateTime sentAt;
}