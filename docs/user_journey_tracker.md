# User Journey Tracker

This document tracks verified end-to-end user journeys and operational flows in the Search Engine.

---

## 🗺️ Journeys

### 1. Document Event Routing (Ingestion Trigger)
*   **Actor**: Client / Ingestion Trigger
*   **Description**: A user or scheduler sends a new document notification to the `document-service` ingestion API. The service automatically classfies the event by type and routes it to the corresponding Kafka topic priority queue.
*   **Status**: Active & Verified

#### 🛠️ API Contract
*   **Endpoint**: `POST http://localhost:8086/document/events/send`
*   **Headers**: `Content-Type: application/json`
*   **Payload Schema (`DocumentEventDto`)**:
    ```json
    {
      "eventId": "string",
      "eventType": "string", // "traffic", "subscription", or "registry"
      "docURL": "string",
      "timestamp": 1719572400
    }
    ```

#### 🚦 Routing Logic & Kafka Consumption Flow
The system resolves the event type to the target Kafka topic, where it is asynchronously consumed by the designated consumer group:

| Event Type | Resolved Kafka Topic | Consumer Method | Consumer Group | Downstream Processing / Service |
| :--- | :--- | :--- | :--- | :--- |
| `subscription` | `docs.subscription.high-priority` | `consumeSubscriptionEvent(...)` | `crawl-service-subscription-group` | `crawlService.processSubscriptionEvent` (Crawl Async Executor -> CrawlerHandler) |
| `traffic` | `docs.traffic.medium-priority` | `consumeTrafficEvent(...)` | `crawl-service-traffic-group` | `TrafficCrawlerBatchAccumulator` -> `crawlService.processTrafficEventBatch` (Parallel Batch) |
| `low` | `docs.registry.low-priority` | `consumeRegistryEvent(...)` | `crawl-service-registry-group` | `crawlService.processRegistryEvent` (Crawl Async Executor -> CrawlerHandler) |

---

#### 💻 Sample Request
```bash
curl -X POST "http://localhost:8086/document/events/send" \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "evt-001",
    "eventType": "subscription",
    "docURL": "https://example.com/docs/spec.pdf",
    "timestamp": 1719572400
  }'
```

#### 📤 Sample Success Response
```json
{
  "status": "sent",
  "topic": "docs.subscription.high-priority",
  "eventId": "evt-001"
}
```
