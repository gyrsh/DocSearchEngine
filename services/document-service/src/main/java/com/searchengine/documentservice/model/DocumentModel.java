package com.searchengine.documentservice.model;

import com.searchengine.documentservice.dto.DocumentEventDto;

public class DocumentModel {
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

    public static DocumentModel fromEvent(DocumentEventDto eventDto) {
        DocumentModel model = new DocumentModel();
        model.setEventId(eventDto.getEventId());
        model.setEventType(eventDto.getEventType());
        model.setDocURL(eventDto.getDocURL());
        model.setTimestamp(eventDto.getTimestamp());
        return model;
    }

    @Override
    public String toString() {
        return "DocumentModel{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", docURL='" + docURL + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
