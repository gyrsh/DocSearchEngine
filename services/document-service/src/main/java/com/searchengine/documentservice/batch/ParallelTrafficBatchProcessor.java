package com.searchengine.documentservice.batch;

import com.searchengine.documentservice.dto.DocumentEventDto;
import com.searchengine.documentservice.handler.TrafficDocumentHandler;
import com.searchengine.documentservice.model.DocumentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ParallelTrafficBatchProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ParallelTrafficBatchProcessor.class);

    private final long shutdownTimeoutSeconds;
    private final TrafficDocumentHandler trafficHandler;
    private final ThreadPoolTaskExecutor executor;

    public ParallelTrafficBatchProcessor(TrafficDocumentHandler trafficHandler,
                                         @Qualifier("trafficTaskExecutor") ThreadPoolTaskExecutor executor,
                                         @Value("${document.batch-processing.traffic.shutdown-timeout-seconds:30}") long shutdownTimeoutSeconds) {
        this.trafficHandler = trafficHandler;
        this.executor = executor;
        this.shutdownTimeoutSeconds = shutdownTimeoutSeconds;
        logger.info("Created parallel batch processor with thread pool {}", executor.getThreadNamePrefix());
    }


    public void processBatchInParallel(List<DocumentEventDto> events) {
        if (events == null || events.isEmpty()) {
            logger.warn("Attempted to process empty batch");
            return;
        }

        long startTime = System.currentTimeMillis();
        int totalDocuments = events.size();
        logger.info("Starting parallel batch processing: {} documents", totalDocuments);

        /*
         * 1. INITIALIZE THE BARRIER
         * Set the latch counter to the total number of tasks. The main thread
         * will use this to know exactly how many operations it needs to wait for.
         */

        CountDownLatch latch = new CountDownLatch(totalDocuments);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (DocumentEventDto eventDto : events) {
            executor.execute(() -> {
                try {
                    if (eventDto.getEventType() == null) {
                        logger.warn("Skipping traffic event with missing eventType: {}", eventDto);
                        errorCount.incrementAndGet();
                        return;
                    }

                    DocumentModel document = DocumentModel.fromEvent(eventDto);
                    trafficHandler.handle(document, eventDto);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    logger.error("Error processing traffic document: {}", eventDto, e);
                    errorCount.incrementAndGet();
                } finally {
                    /*
                     * 2. SIGNAL TASK COMPLETION
                     * This decrements the latch counter by 1. Placing it in 'finally'
                     * guarantees the counter drops whether the task succeeds, fails,
                     * or is skipped due to validation rules.
                     */
                    latch.countDown();
                }
            });
        }

        try {
            /*
             * 3. BLOCK THE MAIN THREAD
             * The main thread pauses here and waits until the latch counter reaches 0.
             * The timeout protects the application from freezing forever if a background
             * thread gets stuck or hangs indefinitely.
             */
            boolean completed = latch.await(shutdownTimeoutSeconds, TimeUnit.SECONDS);
            if (!completed) {
                logger.warn("Batch processing timeout after {} seconds. {} documents may not have completed",
                        shutdownTimeoutSeconds, latch.getCount());
            }

            long elapsedMs = System.currentTimeMillis() - startTime;
            logger.info("Batch processing completed. Total: {}, Success: {}, Errors: {} (Time: {}ms)",
                    totalDocuments, successCount.get(), errorCount.get(), elapsedMs);
        } catch (InterruptedException e) {
            logger.error("Batch processing interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        try {
            logger.info("Shutting down parallel batch processor");
            executor.shutdown();
            var underlying = executor.getThreadPoolExecutor();
            if (underlying != null && !underlying.awaitTermination(shutdownTimeoutSeconds, TimeUnit.SECONDS)) {
                logger.warn("Executor did not terminate in time. Forcing shutdown.");
                underlying.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted during shutdown", e);
            var underlying = executor.getThreadPoolExecutor();
            if (underlying != null) {
                underlying.shutdownNow();
            }
            Thread.currentThread().interrupt();
        }
    }
}
