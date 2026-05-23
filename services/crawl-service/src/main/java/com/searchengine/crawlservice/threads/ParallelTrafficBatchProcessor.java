package com.searchengine.crawlservice.threads;

import com.searchengine.crawlservice.handler.CrawlerHandler;
import com.searchengine.crawlservice.model.CrawlerEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelTrafficBatchProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ParallelTrafficBatchProcessor.class);


    private final long shutdownTimeoutSeconds;
    private final CrawlerHandler crawlerHandler;
    private final ThreadPoolTaskExecutor executor;

    public ParallelTrafficBatchProcessor(@Value("${crawler.batch-processing.traffic.shutdown-timeout-seconds:30}")
                                         long shutdownTimeoutSeconds,
                                         CrawlerHandler crawlerHandler,
                                         @Qualifier("trafficTaskExecutor") ThreadPoolTaskExecutor executor) {
        this.shutdownTimeoutSeconds = shutdownTimeoutSeconds;
        this.crawlerHandler = crawlerHandler;
        this.executor = executor;
        logger.info("Created parallel batch processor with thread pool {}", executor.getThreadNamePrefix());

    }

    public void processBatchInParallel(List<CrawlerEventDto> crawlerEventDtoList) {
        //Check if the event is null
        if (crawlerEventDtoList.isEmpty()) {
            logger.warn("Attempted to process empty batch");
            return;
        }
        long startTime = System.currentTimeMillis();
        int totalDocuments = crawlerEventDtoList.size();

        logger.info("Starting parallel batch processing: {} documents", totalDocuments);

        CountDownLatch latch = new CountDownLatch(totalDocuments);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (CrawlerEventDto crawlerEventDto : crawlerEventDtoList) {
            executor.execute(() -> {
                try {
                    if (crawlerEventDto.getEventType() == null) {
                        logger.warn("Skipping traffic event with missing eventType: {}", crawlerEventDto);
                        failureCount.incrementAndGet();
                        return;
                    }

                    crawlerHandler.handle(crawlerEventDto);
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    logger.error("Error processing traffic document: {}", crawlerEventDto, e);
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }

            });
        }

        try {
            /*
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
                    totalDocuments, successCount.get(), failureCount.get(), elapsedMs);
        } catch (InterruptedException e) {
            logger.error("Batch processing interrupted", e);
            Thread.currentThread().interrupt();
        }
        return;

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
