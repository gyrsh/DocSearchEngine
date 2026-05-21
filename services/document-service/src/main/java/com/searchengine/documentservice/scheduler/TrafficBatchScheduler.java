package com.searchengine.documentservice.scheduler;

import com.searchengine.documentservice.batch.TrafficDocumentBatchAccumulator;
import com.searchengine.documentservice.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class TrafficBatchScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TrafficBatchScheduler.class);
    private final TrafficDocumentBatchAccumulator batchAccumulator;
    private final DocumentService documentService;

    public TrafficBatchScheduler(TrafficDocumentBatchAccumulator batchAccumulator,
                                DocumentService documentService) {
        this.batchAccumulator = batchAccumulator;
        this.documentService = documentService;
    }

    @Scheduled(fixedDelayString = "${document.batch-processing.traffic.batch-timeout-ms:5000}")
    public void flushPendingBatch() {
        var pendingBatch = batchAccumulator.getBatchIfReady();
        if (!pendingBatch.isEmpty()) {
            logger.info("Flushing traffic batch with {} documents", pendingBatch.size());
            documentService.processTrafficEventBatch(pendingBatch);
        }
    }
}
