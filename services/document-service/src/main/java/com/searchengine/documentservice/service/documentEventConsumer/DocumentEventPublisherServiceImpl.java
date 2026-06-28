package com.searchengine.documentservice.service.documentEventConsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchengine.documentservice.dao.DocumentDAO;
import com.searchengine.documentservice.dto.DocumentEventDto;
import com.searchengine.documentservice.utils.NormaliserUtils;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.searchengine.shared.models.Document;

@Service
public class DocumentEventPublisherServiceImpl implements DocumentEventPublisherService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final DocumentDAO documentDAO;

    @Value("${document.events.topics.subscription}")
    private String subscriptionTopic;

    @Value("${document.events.topics.traffic}")
    private String trafficTopic;

    @Value("${document.events.topics.registry}")
    private String registryTopic;

    public DocumentEventPublisherServiceImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
            DocumentDAO documentDAO) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.documentDAO = documentDAO;
    }

    @Override
    public String publish(DocumentEventDto eventDto) {
        String topic = resolveTopic(eventDto.getEventType());
        if (topic == null) {
            throw new IllegalArgumentException("Invalid priority. Allowed values: high, medium, low");
        }

        try {
            String payload = objectMapper.writeValueAsString(eventDto);
            String documentId = storeDocument(eventDto);
            kafkaTemplate.send(topic, eventDto.getEventId(), payload);
            return topic;
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize event payload", ex);
        }
    }

    private String resolveTopic(String evenType) {
        if (evenType == null) {
            return null;
        }

        return switch (evenType.toLowerCase(Locale.ROOT)) {
            case "subscription" -> subscriptionTopic;
            case "traffic" -> trafficTopic;
            case "low" -> registryTopic;
            default -> null;
        };
    }

    private String storeDocument(DocumentEventDto documentEventDto) {
        // Check if the document already exists using URL as index

        // Save in Postgres after normalisation
        String url = NormaliserUtils.getCleanURL(documentEventDto.getDocURL());
        Document document = new Document();
        document.setDocumentId(documentEventDto.getEventId());
        document.setRawStorageUri(url);
        document.setVersion(1L);
        document.setCreatedAt(java.time.Instant.ofEpochMilli(documentEventDto.getTimestamp()));
        document.setUpdatedAt(java.time.Instant.now());
        documentDAO.save(document);
        return document.getDocumentId();
    }
}
