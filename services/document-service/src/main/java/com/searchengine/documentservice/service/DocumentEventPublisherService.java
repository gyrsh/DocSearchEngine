package com.searchengine.documentservice.service;

import com.searchengine.documentservice.dto.DocumentEventDto;

public interface DocumentEventPublisherService {
    String publish(String priority, DocumentEventDto eventDto);
}

