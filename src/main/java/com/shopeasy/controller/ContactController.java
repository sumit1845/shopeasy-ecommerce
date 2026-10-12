package com.shopeasy.controller;

import com.shopeasy.dto.ContactRequest;
import com.shopeasy.model.ContactMessage;
import com.shopeasy.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitMessage(@Valid @RequestBody ContactRequest request) {
        ContactMessage saved = contactService.saveMessage(request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Thank you! Your message has been received.",
                "id", saved.getId()
        ));
    }

    @GetMapping
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        return ResponseEntity.ok(contactService.getAllMessages());
    }
}
