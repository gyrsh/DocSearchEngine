package com.searchengine.documentservice.dto;

public class DocumentEventDto {
    private String eventId;
    private String eventType;
    private String docURL;
    private long timestamp;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", docURL='" + docURL + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
