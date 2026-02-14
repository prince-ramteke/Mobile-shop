package com.shopmanager.service;

import com.shopmanager.dto.customer.PaymentReminderDTO;
import com.shopmanager.entity.Customer;
import com.shopmanager.repository.CustomerRepository;
import com.shopmanager.repository.SaleRepository;
import com.shopmanager.repository.RepairJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerReminderService {

    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;
    private final RepairJobRepository repairJobRepository;

    public List<PaymentReminderDTO> getPendingReminders() {

        List<Customer> customers = customerRepository.findAll();
        List<PaymentReminderDTO> reminders = new ArrayList<>();

        for (Customer c : customers) {

            BigDecimal salePending = saleRepository.sumPendingByCustomerId(c.getId());
            BigDecimal repairPending = repairJobRepository.sumPendingByCustomerId(c.getId());

            if (salePending == null) salePending = BigDecimal.ZERO;
            if (repairPending == null) repairPending = BigDecimal.ZERO;

            BigDecimal totalPending = salePending.add(repairPending);

            if (totalPending.compareTo(BigDecimal.ZERO) > 0) {

                String message = """
Hello %s,

This is a reminder from Saurabh Mobile Shop 📱

Your pending amount is ₹%s.
Please clear your dues at your convenience.

Thank you 🙏
""".formatted(c.getName(), totalPending);

// Encode message for URL
                String encodedMsg = java.net.URLEncoder.encode(
                        message,
                        java.nio.charset.StandardCharsets.UTF_8
                );

// Create WhatsApp click link
                String cleanPhone = formatPhone(c.getPhone());
                String whatsappLink = "https://wa.me/91" + cleanPhone + "?text=" + encodedMsg;

                reminders.add(
                        new PaymentReminderDTO(
                                c.getId(),
                                c.getName(),
                                c.getPhone(),
                                totalPending,
                                message,
                                whatsappLink
                        )
                );

            }
        }

        return reminders;
    }

    private String formatPhone(String phone) {
        if (phone == null) return "";

        // remove spaces, +, -, etc
        phone = phone.replaceAll("[^0-9]", "");

        // remove starting 0 if exists
        if (phone.startsWith("0")) {
            phone = phone.substring(1);
        }

        // remove country code if already present
        if (phone.startsWith("91") && phone.length() > 10) {
            phone = phone.substring(2);
        }

        return phone;
    }

}