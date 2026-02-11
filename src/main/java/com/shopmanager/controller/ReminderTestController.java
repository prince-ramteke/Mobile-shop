package com.shopmanager.controller;

import com.shopmanager.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReminderTestController {

    private final ReminderService reminderService;

    @PostMapping("/send-due-reminders")
    public ResponseEntity<String> testReminders() {
        reminderService.sendDueReminders();
        return ResponseEntity.ok("Reminders executed");
    }
}