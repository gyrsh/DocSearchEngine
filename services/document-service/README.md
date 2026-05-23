# Document Service

`document-service` consumes document events from Kafka and processes them by topic-specific flow:
- `subscription-events` -> async handler execution
- `traffic-events` -> batch accumulation + parallel processing
- `registry-events` -> async handler execution

## Code Flow Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    participant Kafka as Kafka Broker
    participant Consumer as DocumentEventConsumer
    participant Acc as TrafficDocumentBatchAccumulator
    participant Sched as TrafficBatchScheduler
    participant Service as DocumentServiceImpl
    participant SubExec as subscriptionTaskExecutor
    participant RegExec as registryTaskExecutor
    participant BatchProc as ParallelTrafficBatchProcessor
    participant TrafficExec as trafficTaskExecutor
    participant SubH as SubscriptionDocumentHandler
    participant TrafH as TrafficDocumentHandler
    participant RegH as RegistryDocumentHandler

    alt Subscription Topic (`subscription-events`)
        Kafka->>Consumer: consumeSubscriptionEvent(message)
        Consumer->>Consumer: ObjectMapper.readValue(...)
        Consumer->>Service: processSubscriptionEvent(eventDto)
        Service->>Service: DocumentModel.fromEvent(eventDto)
        Service->>SubExec: execute(task)
        SubExec->>SubH: handle(document, eventDto)
        SubH->>SubH: create/update by docType (NEW/UPDATE)
    else Traffic Topic (`traffic-events`)
        Kafka->>Consumer: consumeTrafficEvent(message)
        Consumer->>Consumer: ObjectMapper.readValue(...)
        Consumer->>Acc: add(eventDto)
        Consumer->>Acc: getBatchIfReady()
        alt Batch ready in consumer
            Consumer->>Service: processTrafficEventBatch(batch)
            Service->>BatchProc: processBatchInParallel(batch)
        end
        loop every `batch-timeout-ms` (scheduler)
            Sched->>Acc: getBatchIfReady()
            alt Pending batch exists
                Sched->>Service: processTrafficEventBatch(batch)
                Service->>BatchProc: processBatchInParallel(batch)
            end
        end
        BatchProc->>TrafficExec: execute task per event
        TrafficExec->>TrafH: handle(document, eventDto)
        TrafH->>TrafH: create/update by docType (NEW/UPDATE)
    else Registry Topic (`registry-events`)
        Kafka->>Consumer: consumeRegistryEvent(message)
        Consumer->>Consumer: ObjectMapper.readValue(...)
        Consumer->>Service: processRegistryEvent(eventDto)
        Service->>Service: DocumentModel.fromEvent(eventDto)
        Service->>RegExec: execute(task)
        RegExec->>RegH: handle(document, eventDto)
        RegH->>RegH: create/update by docType (NEW/UPDATE)
    end
```
