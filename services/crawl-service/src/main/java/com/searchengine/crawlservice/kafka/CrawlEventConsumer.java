package com.searchengine.crawlservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchengine.crawlservice.model.CrawlerEventDto;
import com.searchengine.crawlservice.service.CrawlService;
import com.searchengine.crawlservice.threads.TrafficCrawlerBatchAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class CrawlEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CrawlEventConsumer.class);
    private final ObjectMapper objectMapper;
    private final CrawlService crawlService;
    private final TrafficCrawlerBatchAccumulator batchAccumulator;

    public CrawlEventConsumer(ObjectMapper objectMapper, CrawlService crawlService,
                              TrafficCrawlerBatchAccumulator batchAccumulator) {
        this.objectMapper = objectMapper;
        this.crawlService = crawlService;
        this.batchAccumulator = batchAccumulator;
    }

    @KafkaListener(topics = "${crawler.events.topics.subscription}", groupId = "crawl-service-subscription-group")
    public void consumeSubscriptionEvent(@Payload String message) {
        try {
            CrawlerEventDto eventDto = objectMapper.readValue(message, CrawlerEventDto.class);
            logger.info("Received SUBSCRIPTION crawl event: {}", eventDto.getDocURL());
            crawlService.processSubscriptionEvent(eventDto);
        } catch (Exception e) {
            logger.error("Failed to deserialize subscription event: {}", message, e);
        }
    }

    @KafkaListener(topics = "${crawler.events.topics.traffic}", groupId = "crawl-service-traffic-group")
    public void consumeTrafficEvent(@Payload String message) {
        try {
            CrawlerEventDto eventDto = objectMapper.readValue(message, CrawlerEventDto.class);
            logger.info("Received TRAFFIC crawl event: {}", eventDto.getDocURL());
            batchAccumulator.addBatch(eventDto);

            // Check if batch is ready to process
            var readyBatch = batchAccumulator.getBatchIfReady();
            if (!readyBatch.isEmpty()) {
                logger.info("Traffic batch ready. Size: {}", readyBatch.size());
                crawlService.processTrafficEventBatch(readyBatch);
            }
        } catch (Exception e) {
            logger.error("Failed to deserialize traffic event: {}", message, e);
        }
    }

    @KafkaListener(topics = "${crawler.events.topics.registry}", groupId = "crawl-service-registry-group")
    public void consumeRegistryEvent(@Payload String message) {
        try {
            CrawlerEventDto eventDto = objectMapper.readValue(message, CrawlerEventDto.class);
            logger.info("Received REGISTRY crawl event: {}", eventDto.getDocURL());
            crawlService.processRegistryEvent(eventDto);
        } catch (Exception e) {
            logger.error("Failed to deserialize registry event: {}", message, e);
        }
    }
}
