package com.searchengine.documentservice.controller;

import com.searchengine.documentservice.dto.DocumentEventDto;
import com.searchengine.documentservice.service.DocumentEventPublisherService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/document")
public class DocumentController {

    private final DocumentEventPublisherService eventPublisherService;

    public DocumentController(DocumentEventPublisherService eventPublisherService) {
        this.eventPublisherService = eventPublisherService;
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Document Service is running");
    }

    @PostMapping("/events/send")
    public ResponseEntity<?> sendEvent(@RequestParam String priority, @RequestBody DocumentEventDto eventDto) {
        try {
            String topic = eventPublisherService.publish(priority, eventDto);
            return ResponseEntity.ok(Map.of(
                    "status", "sent",
                    "topic", topic,
                    "eventId", eventDto.getEventId()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }
}
