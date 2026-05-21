package com.searchengine.documentservice.handler;

import com.searchengine.documentservice.dto.DocumentEventDto;
import com.searchengine.documentservice.model.DocumentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionDocumentHandler implements DocumentTypeHandler {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionDocumentHandler.class);

    @Override
    public void handle(DocumentModel document, DocumentEventDto eventDto) {
        logger.info("SUBSCRIPTION handler: Processing event. eventId={}, docURL={}, eventType={}",
                document.getEventId(), document.getDocURL(), eventDto.getEventType());

        if ("NEW".equalsIgnoreCase(eventDto.getEventType())) {
            createSubscriptionDocument(document);
        } else if ("UPDATE".equalsIgnoreCase(eventDto.getEventType())) {
            updateSubscriptionDocument(document);
        }
    }

    @Override
    public boolean supports(String documentType) {
        return "subscription".equalsIgnoreCase(documentType);
    }

    private void createSubscriptionDocument(DocumentModel document) {
        logger.info("SUBSCRIPTION: Creating new subscribed document. eventId={}, docURL={}",
                document.getEventId(), document.getDocURL());
        // TODO: Add subscription document creation logic
        // - Store metadata in database
        // - Set up real-time update pipeline
        // - Trigger immediate indexing
    }

    private void updateSubscriptionDocument(DocumentModel document) {
        logger.info("SUBSCRIPTION: Updating subscribed document. eventId={}, docURL={}",
                document.getEventId(), document.getDocURL());
        // TODO: Add subscription document update logic
        // - Refresh metadata
        // - Update index in real-time
        // - FIFO processing guarantees
    }
}
