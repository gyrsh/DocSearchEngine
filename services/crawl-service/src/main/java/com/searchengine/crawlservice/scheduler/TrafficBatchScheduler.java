package com.searchengine.crawlservice.scheduler;

import com.searchengine.crawlservice.model.CrawlerEventDto;
import com.searchengine.crawlservice.service.CrawlService;
import com.searchengine.crawlservice.threads.TrafficCrawlerBatchAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class TrafficBatchScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TrafficBatchScheduler.class);
    private final TrafficCrawlerBatchAccumulator batchAccumulator;
    private final CrawlService crawlService;

    public TrafficBatchScheduler(TrafficCrawlerBatchAccumulator batchAccumulator,
                                 CrawlService crawlService) {
        this.batchAccumulator = batchAccumulator;
        this.crawlService = crawlService;
    }

    @Scheduled(fixedDelayString = "${crawler.batch-processing.traffic.batch-timeout-ms:5000}")
    public void checkForTimeoutBatch() {
        List<CrawlerEventDto> readyBatch = batchAccumulator.getBatchIfReady();
        if (!readyBatch.isEmpty()) {
            logger.info("Traffic batch ready (timeout flush). Size: {}", readyBatch.size());
            crawlService.processTrafficEventBatch(readyBatch);
        }
    }
}
