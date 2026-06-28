package com.searchengine.documentservice.controller;

import com.searchengine.documentservice.dto.DocumentEventDto;
import com.searchengine.documentservice.service.DocumentEventPublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Document Controller", description = "Endpoints for checking service status and sending document events to high/medium/low priority queues.")
public class DocumentController {

    private final DocumentEventPublisherService eventPublisherService;

    public DocumentController(DocumentEventPublisherService eventPublisherService) {
        this.eventPublisherService = eventPublisherService;
    }

    @GetMapping("/status")
    @Operation(summary = "Get Service Status", description = "Returns a text confirmation indicating that the Document Service is healthy and running.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Document Service is running");
    }

    @PostMapping("/events/send")
    @Operation(
        summary = "Send a Document Event",
        description = "Publishes a new document event (containing URL, event type, and timestamps) onto a Kafka topic corresponding to the specified priority."
    )
    @ApiResponse(responseCode = "200", description = "Event was successfully sent to Kafka")
    @ApiResponse(responseCode = "400", description = "Invalid priority parameter or request payload")
    @ApiResponse(responseCode = "500", description = "Internal error publishing the event")
    public ResponseEntity<?> sendEvent(
            @Parameter(description = "Queue priority level: 'high', 'medium', or 'low'", required = true, example = "high")
            @RequestParam String priority,
            @RequestBody DocumentEventDto eventDto) {
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
