package com.searchengine.documentservice.handler;

import com.searchengine.documentservice.dto.DocumentEventDto;
import com.searchengine.documentservice.model.DocumentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TrafficDocumentHandler implements DocumentTypeHandler {

    private static final Logger logger = LoggerFactory.getLogger(TrafficDocumentHandler.class);
    private static final long CRAWL_INTERVAL_MILLIS = 3600000; // 1 hour

    @Override
    public void handle(DocumentModel document, DocumentEventDto eventDto) {
        logger.info("TRAFFIC handler: Processing event. eventId={}, docURL={}, eventType={}",
                document.getEventId(), document.getDocURL(), eventDto.getEventType());

        if ("NEW".equalsIgnoreCase(eventDto.getEventType())) {
            createTrafficBasedDocument(document);
        } else if ("UPDATE".equalsIgnoreCase(eventDto.getEventType())) {
            updateTrafficBasedDocument(document);
        }
    }

    @Override
    public boolean supports(String documentType) {
        return "traffic".equalsIgnoreCase(documentType);
    }

    private void createTrafficBasedDocument(DocumentModel document) {
        logger.info("TRAFFIC: Creating high-traffic document. eventId={}, docURL={}",
                document.getEventId(), document.getDocURL());
        // TODO: Add traffic-based document creation logic
        // - Store in Traffic DB
        // - Schedule crawl every 1 hour (CRAWL_INTERVAL_MILLIS)
        // - Associate with Query Service traffic metrics
    }

    private void updateTrafficBasedDocument(DocumentModel document) {
        logger.info("TRAFFIC: Updating high-traffic document. eventId={}, docURL={}",
                document.getEventId(), document.getDocURL());
        // TODO: Add traffic-based document update logic
        // - Update traffic metrics in Traffic DB
        // - Adjust crawl frequency based on traffic patterns
        // - Re-evaluate priority based on current traffic
    }
}
