package com.searchengine.crawlservice.threads;

import com.searchengine.crawlservice.model.CrawlerEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class TrafficCrawlerBatchAccumulator {

    private static final Logger logger = LoggerFactory.getLogger(TrafficCrawlerBatchAccumulator.class);
    private final ReentrantLock lock = new ReentrantLock();

    @Value("${crawler.batch-processing.traffic.batch-size:100}")
    private int batchSize;

    @Value("${crawler.batch-processing.traffic.batch-timeout-ms:5000}")
    private long batchTimeoutMs;

    private volatile List<CrawlerEventDto> batch = new ArrayList<>();
    private volatile long lastProcessedTime = System.currentTimeMillis();

    public void addBatch(CrawlerEventDto crawlerEventDto) {
        lock.lock();
        try {
            batch.add(crawlerEventDto);
            logger.debug("Added event to batch. Current batch size: {}", batch.size());
        } finally {
            lock.unlock();
        }
    }

    public List<CrawlerEventDto> getBatchIfReady() {
        lock.lock();
        try {
            if (isBatchReady()) {
                List<CrawlerEventDto> currentBatch = new ArrayList<>(batch);
                batch.clear();
                lastProcessedTime = System.currentTimeMillis();
                logger.debug("Batch is ready. Returning {} documents", currentBatch.size());
                return currentBatch;
            }
            return new ArrayList<>();

        } finally {
            lock.unlock();
        }
    }

    private boolean isBatchReady() {
        if (batch.size() >= batchSize) {
            logger.debug("Batch size threshold reached: {} >= {}", batch.size(), batchSize);
            return true;
        }
        long elapsedMs = System.currentTimeMillis() - lastProcessedTime;
        boolean timeoutReached = elapsedMs >= batchTimeoutMs && !batch.isEmpty();
        if (timeoutReached) {
            logger.debug("Batch timeout reached: {} ms >= {} ms", elapsedMs, batchTimeoutMs);
        }
        return timeoutReached;
    }

    public int getCurrentBatchSize() {
        lock.lock();
        try {
            return batch.size();
        } finally {
            lock.unlock();
        }
    }

}

