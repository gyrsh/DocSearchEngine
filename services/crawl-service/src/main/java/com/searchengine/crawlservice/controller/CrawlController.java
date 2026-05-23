package com.searchengine.crawlservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crawl")
public class CrawlController {

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Crawl Service is running");
    }

    @PostMapping("/crawl")
    public ResponseEntity<String> docCrawl() {
        return ResponseEntity.ok("Crawl Service is running");
    }
}
