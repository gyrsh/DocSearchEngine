package com.searchengine.documentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchengine.documentservice.dto.DocumentEventDto;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DocumentEventPublisherServiceImpl implements DocumentEventPublisherService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${document.events.topics.subscription}")
    private String subscriptionTopic;

    @Value("${document.events.topics.traffic}")
    private String trafficTopic;

    @Value("${document.events.topics.registry}")
    private String registryTopic;

    public DocumentEventPublisherServiceImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String publish(String priority, DocumentEventDto eventDto) {
        String topic = resolveTopic(priority);
        if (topic == null) {
            throw new IllegalArgumentException("Invalid priority. Allowed values: high, medium, low");
        }

        try {
            String payload = objectMapper.writeValueAsString(eventDto);
            kafkaTemplate.send(topic, eventDto.getEventId(), payload);
            return topic;
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize event payload", ex);
        }
    }

    private String resolveTopic(String priority) {
        if (priority == null) {
            return null;
        }

        return switch (priority.toLowerCase(Locale.ROOT)) {
            case "high" -> subscriptionTopic;
            case "medium" -> trafficTopic;
            case "low" -> registryTopic;
            default -> null;
        };
    }
}

