package com.searchengine.shared.models;

import java.time.Instant;

public class CrawledDocumentContent {

    // Foreign Key -> CrawledDocument.documentId
    // Identifies which document this content belongs to.
    private String documentId;

    // Plain text extracted from the original document.
    // Example:
    // PDF -> Text
    // HTML -> Visible Text
    // This becomes the source for fragment generation.
    private String extractedText;

    // Length of extracted text.
    // Useful for analytics, validation and chunking decisions.
    private Long textLength;

    // Content version.
    // Incremented whenever document content changes.
    // Used for re-indexing and Kafka event ordering.
    private Integer version;

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

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public Long getTextLength() {
        return textLength;
    }

    public void setTextLength(Long textLength) {
        this.textLength = textLength;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

