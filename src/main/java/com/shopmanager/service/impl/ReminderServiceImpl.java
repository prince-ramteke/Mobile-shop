package com.shopmanager.service.impl;

import com.shopmanager.entity.DueEntry;
import com.shopmanager.entity.MessageLog;
import com.shopmanager.entity.enums.DueStatus;
import com.shopmanager.entity.enums.MessageChannel;
import com.shopmanager.entity.enums.MessageStatus;
import com.shopmanager.entity.enums.MessageType;
import com.shopmanager.message.dto.MessagePayload;
import com.shopmanager.message.service.NotificationService;
import com.shopmanager.repository.DueEntryRepository;
import com.shopmanager.repository.MessageLogRepository;
import com.shopmanager.service.ReminderService;
import com.shopmanager.settings.service.ShopSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import com.shopmanager.audit.service.AuditService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReminderServiceImpl implements ReminderService {

    private final DueEntryRepository dueEntryRepository;
    private final MessageLogRepository messageLogRepository;
    private final NotificationService notificationService;
    private final ShopSettingsService shopSettingsService;

    private final AuditService auditService;

    @Override
    public void sendDueReminders() {

        log.info("📨 Sending WhatsApp due reminders");

        List<DueEntry> dues = dueEntryRepository.findByStatusIn(
                List.of(DueStatus.OPEN, DueStatus.PARTIALLY_PAID)
        );

        for (DueEntry due : dues) {

            if (due.getLastPaymentDate() == null) continue;

            long overdueDays = ChronoUnit.DAYS.between(
                    due.getLastPaymentDate().toLocalDate(),
                    LocalDate.now()
            );

            if (overdueDays <= 0) continue;

            // 🔒 WhatsApp enabled check
            if (!shopSettingsService.getSettings().getWhatsappEnabled()) {
                log.info("🚫 WhatsApp disabled. Skipping reminder for customer {}", due.getCustomer().getName());
                continue;
            }

            // 🔒 Anti-spam check (DB driven)
            Optional<MessageLog> lastReminder =
                    messageLogRepository.findTopByCustomerIdAndTypeAndChannelOrderBySentAtDesc(
                            due.getCustomer().getId(),
                            MessageType.REMINDER,
                            MessageChannel.WHATSAPP
                    );

            if (lastReminder.isPresent()) {

                long daysSinceLastReminder = ChronoUnit.DAYS.between(
                        lastReminder.get().getSentAt().toLocalDate(),
                        LocalDate.now()
                );

                int gapDays = shopSettingsService.getSettings().getReminderGapDays();

                if (daysSinceLastReminder < gapDays) {
                    log.info(
                            "⏭️ Skipping reminder for customer {} (sent {} days ago)",
                            due.getCustomer().getName(),
                            daysSinceLastReminder
                    );
                    continue;
                }
            }

            try {
                MessageLog logEntry = messageLogRepository.save(
                        MessageLog.builder()
                                .customer(due.getCustomer())
                                .type(MessageType.REMINDER)
                                .channel(MessageChannel.WHATSAPP)
                                .status(MessageStatus.PENDING)
                                .messageContent(
                                        "Due reminder | Pending ₹" + due.getPendingAmount()
                                )
                                .build()
                );

                MessagePayload payload = MessagePayload.builder()
                        .recipient(due.getCustomer().getWhatsappNumber())
                        .message(
                                "🔔 Payment Reminder\n\n" +
                                        "Dear " + due.getCustomer().getName() + ",\n" +
                                        "Your pending amount is ₹" + due.getPendingAmount() + ".\n" +
                                        "Overdue by " + overdueDays + " days.\n\n" +
                                        "Please visit the shop or pay at earliest.\n\n" +
                                        "Thank you 🙏"
                        )
                        .build();

                notificationService.send(logEntry, payload);
                auditService.log(
                        "SEND_REMINDER",
                        "DUE",
                        due.getId(),
                        "Pending ₹" + due.getPendingAmount()
                );

                log.info("✅ Reminder sent to {}", due.getCustomer().getWhatsappNumber());

            } catch (Exception e) {
                log.error("❌ Failed to send reminder for due {}", due.getId(), e);
            }
        }
    }
}