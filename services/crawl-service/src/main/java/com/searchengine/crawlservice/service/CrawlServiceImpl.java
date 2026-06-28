package com.searchengine.crawlservice.service;

import com.searchengine.crawlservice.handler.CrawlerHandler;
import com.searchengine.crawlservice.model.CrawlerEventDto;
import com.searchengine.crawlservice.threads.ParallelTrafficBatchProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrawlServiceImpl implements CrawlService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlServiceImpl.class);
    private final CrawlerHandler crawlerHandler;
    private final ParallelTrafficBatchProcessor parallelBatchProcessor;
    private final ThreadPoolTaskExecutor subscriptionTaskExecutor;
    private final ThreadPoolTaskExecutor registryTaskExecutor;

    public CrawlServiceImpl(
            CrawlerHandler crawlerHandler,
            ParallelTrafficBatchProcessor parallelBatchProcessor,
            @Qualifier("subscriptionTaskExecutor") ThreadPoolTaskExecutor subscriptionTaskExecutor,
            @Qualifier("registryTaskExecutor") ThreadPoolTaskExecutor registryTaskExecutor) {
        this.crawlerHandler = crawlerHandler;
        this.parallelBatchProcessor = parallelBatchProcessor;
        this.subscriptionTaskExecutor = subscriptionTaskExecutor;
        this.registryTaskExecutor = registryTaskExecutor;
    }

    @Override
    public void processSubscriptionEvent(CrawlerEventDto eventDto) {
        if (eventDto.getEventType() == null) {
            logger.warn("Skipping subscription event with missing eventType: {}", eventDto);
            return;
        }

        logger.info("Scheduling SUBSCRIPTION document processing asynchronously: {}", eventDto.getDocURL());
        subscriptionTaskExecutor.execute(() -> {
            try {
                crawlerHandler.handle(eventDto);
            } catch (Exception e) {
                logger.error("Error processing subscription event: {}", eventDto, e);
            }
        });
    }

    @Override
    public void processTrafficEventBatch(List<CrawlerEventDto> events) {
        logger.info("Processing TRAFFIC batch with {} documents (parallel)", events.size());
        parallelBatchProcessor.processBatchInParallel(events);
    }

    @Override
    public void processRegistryEvent(CrawlerEventDto eventDto) {
        if (eventDto.getEventType() == null) {
            logger.warn("Skipping registry event with missing eventType: {}", eventDto);
            return;
        }

        logger.info("Scheduling REGISTRY document processing asynchronously: {}", eventDto.getDocURL());
        registryTaskExecutor.execute(() -> {
            try {
                crawlerHandler.handle(eventDto);
            } catch (Exception e) {
                logger.error("Error processing registry event: {}", eventDto, e);
            }
        });
    }
}
