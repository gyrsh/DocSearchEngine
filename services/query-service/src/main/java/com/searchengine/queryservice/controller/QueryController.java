package com.searchengine.queryservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/query")
@Tag(name = "Query Controller", description = "Endpoints for checking query service status and executing full-text document searches.")
public class QueryController {

    @GetMapping("/status")
    @Operation(summary = "Get Service Status", description = "Returns a text confirmation indicating that the Query Service is healthy and running.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Query Service is running");
    }
}
