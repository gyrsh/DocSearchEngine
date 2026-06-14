package com.searchengine.shared.models;

import java.time.Instant;

public class Document {

    // Primary Key
    // Unique identifier shared across all services.
    private String documentId;

    // Human-readable document title.
    // Displayed in search results.
    private String title;

    // Location of original document.
    // Example:
    // s3://raw/doc1.pdf
    // local://documents/doc1.pdf
    private String rawStorageUri;

    // Location of extracted text.
    // Useful when extracted text becomes too large
    // to store directly in PostgreSQL.
    private String textStorageUri;

    // Current document version.
    // Incremented whenever content changes.
    private Long version;

    // Record creation timestamp.
    private Instant createdAt;

    // Record update timestamp.
    private Instant updatedAt;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRawStorageUri() {
        return rawStorageUri;
    }

    public void setRawStorageUri(String rawStorageUri) {
        this.rawStorageUri = rawStorageUri;
    }

    public String getTextStorageUri() {
        return textStorageUri;
    }

    public void setTextStorageUri(String textStorageUri) {
        this.textStorageUri = textStorageUri;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
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