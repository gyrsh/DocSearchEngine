package com.searchengine.indexingservice.controller;

import com.searchengine.indexingservice.service.SearchIndexService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
@Tag(name = "Indexing Controller", description = "Endpoints for checking indexing service status and performing search index management.")
public class IndexingController {

    private final SearchIndexService searchIndexService;

    public IndexingController(SearchIndexService searchIndexService) {
        this.searchIndexService = searchIndexService;
    }

    @GetMapping("/status")
    @Operation(summary = "Get Service Status", description = "Returns a text confirmation indicating that the Indexing Service is healthy and running.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Indexing Service is running");
    }
}
