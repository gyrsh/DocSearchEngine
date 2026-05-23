# Document Service - Kafka Architecture & Local Setup

This module is responsible for prioritizing and routing documents using Apache Kafka before storing them or feeding them into downstream services.

---

## 🏗️ 3-Tier Priority Architecture

To manage processing speeds and resource consumption, documents are routed across three separate Kafka topics:


| Priority | Document Tier | Kafka Topic Name | Processing Strategy |
| :--- | :--- | :--- | :--- |
| 🥇 **Highest** | Subscription-Based | `docs.subscription.high-priority` | Real-time, Strict FIFO ordering (Key-based routing) |
| 🥈 **Medium** | High-Traffic | `docs.traffic.medium-priority` | High-throughput batch consumption (`max-poll-records: 500`) |
| 🥉 **Lowest** | Registry-Based | `docs.registry.low-priority` | Low-concurrency, slow background trickle processing |

---

## 🚀 Local Infrastructure Setup (Docker & Kafdrop)

Follow these steps to run a fully configured Kafka broker and its Web UI (`Kafdrop`) on your local machine.

### 1. Create a Shared Docker Network
```bash
docker network create kafka-net
```

### 2. Start the Kafka Broker Container
```bash
docker run -d --name kafka-broker \
  -p 9092:9092 \
  --network kafka-net \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka-broker:9092 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@kafka-broker:9093 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  apache/kafka:latest
```

### 3. Start the Kafdrop UI Container
```bash
docker run -d --name kafdrop-ui \
  -p 9000:9000 \
  --network kafka-net \
  -e KAFKA_BROKERCONNECT=kafka-broker:9092 \
  obsidiandynamics/kafdrop:latest
```

👉 **Access the Dashboard:** Open your browser and navigate to **http://localhost:9000** to visually inspect topics, partitions, and message logs.

---

## 🛠️ Testing Your Setup Locally

### Standard Message Payload (JSON POJO)
All priorities share the following structural event schema:

```json
{
  "eventId": "1234$20i023",
  "docType": "NEW",
  "docURL": "http://abc.com",
  "timestamp": 17356490
}
```

### Option A: Test via the Command Line (CLI Producer)
You can mock a producer and inject messages directly into Kafka without launching the Spring application:

1. Exec into the running container network:
   ```bash
   docker exec -it kafka-broker kafka-console-producer --bootstrap-server localhost:9092 --topic docs.subscription.high-priority
   ```
2. Paste the JSON string into the prompt `>` and hit **Enter**.

### Option B: Test via Spring Boot Application
If you built the temporary REST endpoint (`/api/test-kafka/send`), execute a `POST` request to test your custom routing rules:

```bash
curl -X POST "http://localhost:8080/api/test-kafka/send?priority=high" \
     -H "Content-Type: application/json" \
     -d '{"eventId": "1234$20i023", "docType": "NEW", "docURL": "http://abc.com", "timestamp": 17356490}'
```
