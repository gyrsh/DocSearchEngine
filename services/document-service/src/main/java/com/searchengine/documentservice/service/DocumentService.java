package com.searchengine.documentservice.service;

import com.searchengine.documentservice.dto.DocumentEventDto;
import java.util.List;

public interface DocumentService {
    void processSubscriptionEvent(DocumentEventDto eventDto);
    void processTrafficEventBatch(List<DocumentEventDto> events);
    void processRegistryEvent(DocumentEventDto eventDto);
}

