package com.searchengine.dedupservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dedup")
@Tag(name = "Dedup Controller", description = "Endpoints for checking dedup service status and managing document duplication detection.")
public class DedupController {

    @GetMapping("/status")
    @Operation(summary = "Get Service Status", description = "Returns a text confirmation indicating that the Dedup Service is healthy and running.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Dedup Service is running");
    }
}
