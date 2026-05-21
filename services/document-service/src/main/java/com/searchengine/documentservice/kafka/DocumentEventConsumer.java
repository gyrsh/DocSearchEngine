package com.searchengine.documentservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchengine.documentservice.batch.TrafficDocumentBatchAccumulator;
import com.searchengine.documentservice.dto.DocumentEventDto;
import com.searchengine.documentservice.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class DocumentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DocumentEventConsumer.class);
    private final ObjectMapper objectMapper;
    private final DocumentService documentService;
    private final TrafficDocumentBatchAccumulator batchAccumulator;

    @Value("${document.events.topics.subscription}")
    private String subscriptionTopic;

    @Value("${document.events.topics.traffic}")
    private String trafficTopic;

    @Value("${document.events.topics.registry}")
    private String registryTopic;

    public DocumentEventConsumer(ObjectMapper objectMapper, DocumentService documentService,
                                TrafficDocumentBatchAccumulator batchAccumulator) {
        this.objectMapper = objectMapper;
        this.documentService = documentService;
        this.batchAccumulator = batchAccumulator;
    }

    @KafkaListener(topics = "${document.events.topics.subscription}", groupId = "document-service-subscription-group")
    public void consumeSubscriptionEvent(@Payload String message) {
        try {
            DocumentEventDto eventDto = objectMapper.readValue(message, DocumentEventDto.class);
            logger.info("Received SUBSCRIPTION event: {}", eventDto);
            documentService.processSubscriptionEvent(eventDto);
        } catch (Exception e) {
            logger.error("Failed to deserialize subscription event: {}", message, e);
        }
    }

    @KafkaListener(topics = "${document.events.topics.traffic}", groupId = "document-service-traffic-group")
    public void consumeTrafficEvent(@Payload String message) {
        try {
            DocumentEventDto eventDto = objectMapper.readValue(message, DocumentEventDto.class);
            logger.info("Received TRAFFIC event: {}", eventDto);
            batchAccumulator.add(eventDto);
            
            // Check if batch is ready to process
            var readyBatch = batchAccumulator.getBatchIfReady();
            if (!readyBatch.isEmpty()) {
                logger.info("Traffic batch ready. Size: {}", readyBatch.size());
                documentService.processTrafficEventBatch(readyBatch);
            }
        } catch (Exception e) {
            logger.error("Failed to deserialize traffic event: {}", message, e);
        }
    }

    @KafkaListener(topics = "${document.events.topics.registry}", groupId = "document-service-registry-group")
    public void consumeRegistryEvent(@Payload String message) {
        try {
            DocumentEventDto eventDto = objectMapper.readValue(message, DocumentEventDto.class);
            logger.info("Received REGISTRY event: {}", eventDto);
            documentService.processRegistryEvent(eventDto);
        } catch (Exception e) {
            logger.error("Failed to deserialize registry event: {}", message, e);
        }
    }
}

