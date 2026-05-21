package com.searchengine.documentservice.handler;

import com.searchengine.documentservice.dto.DocumentEventDto;
import com.searchengine.documentservice.model.DocumentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RegistryDocumentHandler implements DocumentTypeHandler {

    private static final Logger logger = LoggerFactory.getLogger(RegistryDocumentHandler.class);

    @Override
    public void handle(DocumentModel document, DocumentEventDto eventDto) {
        logger.info("REGISTRY handler: Processing event. eventId={}, docURL={}, eventType={}",
                document.getEventId(), document.getDocURL(), eventDto.getEventType());

        if ("NEW".equalsIgnoreCase(eventDto.getEventType())) {
            createRegistryDocument(document);
        } else if ("UPDATE".equalsIgnoreCase(eventDto.getEventType())) {
            updateRegistryDocument(document);
        }
    }

    @Override
    public boolean supports(String documentType) {
        return "registry".equalsIgnoreCase(documentType);
    }

    private void createRegistryDocument(DocumentModel document) {
        logger.info("REGISTRY: Creating new unclassified document. eventId={}, docURL={}",
                document.getEventId(), document.getDocURL());
        // TODO: Add registry-based document creation logic
        // - Store in Registry DB
        // - Mark as pending classification
        // - Schedule for classification pipeline
        // - Low priority processing
    }

    private void updateRegistryDocument(DocumentModel document) {
        logger.info("REGISTRY: Updating unclassified document. eventId={}, docURL={}",
                document.getEventId(), document.getDocURL());
        // TODO: Add registry-based document update logic
        // - Update registry metadata
        // - Re-evaluate classification if needed
    }
}
