package com.searchengine.documentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object representing a document processing event.")
public class DocumentEventDto {

    @Schema(description = "Unique UUID identifier for this event instance.", example = "123e4567-e89b-12d3-a456-426614174000")
    private String eventId;

    @Schema(description = "Type of document processing to perform (e.g. registry, subscription, traffic).", example = "traffic")
    private String eventType;

    @Schema(description = "Remote URL or storage path of the document content.", example = "https://example.com/docs/invoice-982.pdf")
    private String docURL;

    @Schema(description = "Epoch millisecond timestamp when the event was generated.", example = "1793284200000")
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
