package com.searchengine.documentservice.batch;

import com.searchengine.documentservice.dto.DocumentEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class TrafficDocumentBatchAccumulator {

    private static final Logger logger = LoggerFactory.getLogger(TrafficDocumentBatchAccumulator.class);

    @Value("${document.batch-processing.traffic.batch-size:100}")
    private int batchSize;

    @Value("${document.batch-processing.traffic.batch-timeout-ms:5000}")
    private long batchTimeoutMs;

    private volatile List<DocumentEventDto> batch = new ArrayList<>();
    private volatile long lastProcessedTime = System.currentTimeMillis();
    private final ReentrantLock lock = new ReentrantLock();

    public void add(DocumentEventDto event) {
        lock.lock();
        try {
            batch.add(event);
            logger.debug("Added event to batch. Current batch size: {}", batch.size());
        } finally {
            lock.unlock();
        }
    }

    public List<DocumentEventDto> getBatchIfReady() {
        lock.lock();
        try {
            if (isBatchReady()) {
                List<DocumentEventDto> currentBatch = new ArrayList<>(batch);
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

    public int getBatchSizeConfig() {
        return batchSize;
    }

    public long getBatchTimeoutMsConfig() {
        return batchTimeoutMs;
    }
}
