package com.searchengine.shared.models;

import java.time.Instant;

public class DocumentMetadata {
    private String docId;
    private String docUrl;
    private Instant timestamp;
    private String stage;
    private String simHash;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getSimHash() {
        return simHash;
    }

    public void setSimHash(String simHash) {
        this.simHash = simHash;
    }
}
