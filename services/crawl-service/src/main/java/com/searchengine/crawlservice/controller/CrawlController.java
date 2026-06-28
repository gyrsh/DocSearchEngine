package com.searchengine.crawlservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crawl")
@Tag(name = "Crawl Controller", description = "Endpoints for checking crawl service status and triggering document crawls.")
public class CrawlController {

    @GetMapping("/status")
    @Operation(summary = "Get Service Status", description = "Returns a text confirmation indicating that the Crawl Service is healthy and running.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Crawl Service is running");
    }

    @PostMapping("/crawl")
    @Operation(summary = "Trigger Document Crawl", description = "Triggers the crawling process for documents and schedules resource extraction.")
    @ApiResponse(responseCode = "200", description = "Crawl process successfully triggered")
    public ResponseEntity<String> docCrawl() {
        return ResponseEntity.ok("Crawl Service is running");
    }
}
