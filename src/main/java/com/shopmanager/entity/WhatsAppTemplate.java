package com.shopmanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "whatsapp_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TemplateType type;

    @Column(length = 2000)
    private String content;

    private boolean enabled;
}