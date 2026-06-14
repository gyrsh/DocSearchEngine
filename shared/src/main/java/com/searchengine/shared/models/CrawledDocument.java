package com.searchengine.shared.models;

import java.time.Instant;
import java.util.Map;

public class CrawledDocument {

    // Primary Key
    // Unique identifier for a document across the entire system.
    private String documentId;

    // Version of the document.
    // Used to prevent stale Kafka events from overwriting newer data.
    private Long version;

    // Original URL discovered by the crawler.
    private String url;

    // Canonical URL after normalization.
    // Helps avoid indexing the same content under multiple URLs.
    private String canonicalUrl;

    // Human-readable document title.
    // Used in search results and debugging.
    private String title;

    // MIME type of the document.
    // Examples:
    // application/pdf
    // text/html
    // text/plain
    // Used to determine which parser should process the document.
    private String contentType;

    // Language of the document.
    // Used for tokenization, stemming and search ranking.
    // Example: en, fr, hi
    private String language;

    // Current crawl lifecycle state.
    // DISCOVERED -> CRAWLING -> CRAWLED
    private CrawlStatus status;

    // HTTP response status received while fetching.
    // Examples: 200, 404, 500
    // Useful for debugging crawl failures.
    private Integer httpStatus;

    // Exact content fingerprint (typically SHA-256).
    // Used for exact duplicate detection and change detection.
    private String contentHash;

    // Similarity-preserving fingerprint.
    // Used for near-duplicate detection using Hamming Distance.
    private Long simhash;

    // Flexible metadata associated with the document.
    // Example:
    // author
    // pageCount
    // source
    private Map<String, Object> metadata;

    // Timestamp when the document was first discovered.
    // Never changes after creation.
    private Instant firstSeenAt;

    // Timestamp of the most recent successful crawl.
    private Instant lastCrawledAt;

    // Timestamp when the scheduler should crawl the document again.
    private Instant nextCrawlAt;

    // Record creation timestamp.
    private Instant createdAt;

    // Record last update timestamp.
    private Instant updatedAt;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public CrawlStatus getStatus() {
        return status;
    }

    public void setStatus(CrawlStatus status) {
        this.status = status;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public Long getSimhash() {
        return simhash;
    }

    public void setSimhash(Long simhash) {
        this.simhash = simhash;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Instant getFirstSeenAt() {
        return firstSeenAt;
    }

    public void setFirstSeenAt(Instant firstSeenAt) {
        this.firstSeenAt = firstSeenAt;
    }

    public Instant getLastCrawledAt() {
        return lastCrawledAt;
    }

    public void setLastCrawledAt(Instant lastCrawledAt) {
        this.lastCrawledAt = lastCrawledAt;
    }

    public Instant getNextCrawlAt() {
        return nextCrawlAt;
    }

    public void setNextCrawlAt(Instant nextCrawlAt) {
        this.nextCrawlAt = nextCrawlAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}