package com.searchengine.documentservice.handler;

import com.searchengine.documentservice.dto.DocumentEventDto;
import com.searchengine.documentservice.model.DocumentModel;

public interface DocumentTypeHandler {
    void handle(DocumentModel document, DocumentEventDto eventDto);
    boolean supports(String documentType);
}
