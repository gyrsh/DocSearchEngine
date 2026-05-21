package com.searchengine.documentservice.service;

import com.searchengine.documentservice.batch.ParallelTrafficBatchProcessor;
import com.searchengine.documentservice.dto.DocumentEventDto;
import com.searchengine.documentservice.handler.RegistryDocumentHandler;
import com.searchengine.documentservice.handler.SubscriptionDocumentHandler;
import com.searchengine.documentservice.model.DocumentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private final SubscriptionDocumentHandler subscriptionHandler;
    private final ParallelTrafficBatchProcessor parallelBatchProcessor;
    private final RegistryDocumentHandler registryHandler;
    private final ThreadPoolTaskExecutor subscriptionTaskExecutor;
    private final ThreadPoolTaskExecutor registryTaskExecutor;

    public DocumentServiceImpl(SubscriptionDocumentHandler subscriptionHandler,
                               ParallelTrafficBatchProcessor parallelBatchProcessor,
                               RegistryDocumentHandler registryHandler,
                               @Qualifier("subscriptionTaskExecutor") ThreadPoolTaskExecutor subscriptionTaskExecutor,
                               @Qualifier("registryTaskExecutor") ThreadPoolTaskExecutor registryTaskExecutor) {
        this.subscriptionHandler = subscriptionHandler;
        this.parallelBatchProcessor = parallelBatchProcessor;
        this.registryHandler = registryHandler;
        this.subscriptionTaskExecutor = subscriptionTaskExecutor;
        this.registryTaskExecutor = registryTaskExecutor;
    }

    @Override
    public void processSubscriptionEvent(DocumentEventDto eventDto) {
        if (eventDto.getEventType() == null) {
            logger.warn("Skipping subscription event with missing eventType: {}", eventDto);
            return;
        }

        DocumentModel document = DocumentModel.fromEvent(eventDto);
        logger.info("Scheduling SUBSCRIPTION document processing: {}", document);

        subscriptionTaskExecutor.execute(() -> {
            try {
                subscriptionHandler.handle(document, eventDto);
            } catch (Exception e) {
                logger.error("Error processing subscription document: {}", eventDto, e);
            }
        });
    }

    @Override
    public void processTrafficEventBatch(List<DocumentEventDto> events) {
        logger.info("Processing TRAFFIC batch with {} documents (parallel)", events.size());
        parallelBatchProcessor.processBatchInParallel(events);
    }

    @Override
    public void processRegistryEvent(DocumentEventDto eventDto) {
        if (eventDto.getEventType() == null) {
            logger.warn("Skipping registry event with missing eventType: {}", eventDto);
            return;
        }

        DocumentModel document = DocumentModel.fromEvent(eventDto);
        logger.info("Scheduling REGISTRY document processing: {}", document);

        registryTaskExecutor.execute(() -> {
            try {
                registryHandler.handle(document, eventDto);
            } catch (Exception e) {
                logger.error("Error processing registry document: {}", eventDto, e);
            }
        });
    }
}
