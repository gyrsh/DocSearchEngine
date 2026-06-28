package com.searchengine.documentservice.service.documentEventConsumer;

import com.searchengine.documentservice.dto.DocumentEventDto;

public interface DocumentEventPublisherService {
    String publish(DocumentEventDto eventDto);
}
