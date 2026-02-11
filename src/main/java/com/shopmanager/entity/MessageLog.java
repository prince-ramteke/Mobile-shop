package com.shopmanager.entity;

import com.shopmanager.entity.enums.MessageChannel;
import com.shopmanager.entity.enums.MessageStatus;
import com.shopmanager.entity.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_logs", indexes = {
        @Index(name = "idx_message_customer", columnList = "customer_id"),
        @Index(name = "idx_message_type", columnList = "type")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // referencing the customer who received the message
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageChannel channel;

    @Column(name = "message_content", columnDefinition = "TEXT", nullable = false, length = 2000)
    private String messageContent;

    @Column(name = "provider_message_id")
    private String providerMessageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;

    @CreatedDate
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;
}