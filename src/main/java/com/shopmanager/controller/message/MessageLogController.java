package com.shopmanager.controller.message;

import com.shopmanager.dto.message.MessageLogRequest;
import com.shopmanager.dto.message.MessageLogResponse;
import com.shopmanager.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageLogController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageLogResponse> create(@RequestBody @Validated MessageLogRequest request) {
        return ResponseEntity.ok(messageService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageLogResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.get(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MessageLogResponse>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(messageService.search(query, page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}