package com.searchengine.crawlservice.model;

public class CrawlerEventDto {
    private String eventType;
    private String docType;
    private String docURL;
    private long timestamp;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocURL() {
        return docURL;
    }

    public void setDocURL(String docURL) {
        this.docURL = docURL;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DocumentEventDto{" +
                ", docType='" + docType + '\'' +
                ", EventType='" + eventType + '\'' +
                ", docURL='" + docURL + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
