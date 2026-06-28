# Search Engine Project: Senior SDE Task Tracker

Use this document to track your progress sequentially. For each task, build/test the code against the defined **Verification Goal** before committing and logging your session.

---

## 🗺️ Progress Dashboard

### 1. Project Scaffolding & Infrastructure (Completed)

| Change ID | Task / Phase | Tech Used | L5 Architecture & Design Notes | Verification Goal (What to Test / Verify) | Status | Commit Link |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CHG-001** | Maven Multi-Module Setup | Maven, POM XML | Isolating shared models from application logic to prevent circular module dependencies. | Build succeeds cleanly via `mvn clean install` from the root directory. | **Completed** | - |
| **CHG-002** | Docker Containerization | Docker Compose | Replicating production-like service setups locally (Postgres, Kafka, Elasticsearch). | Run `docker compose up -d`; check that all containers are healthy. | **Completed** | - |
| **CHG-003** | Shared Domain Models | Java | Decoupling model definitions from specific service execution logic. | Entities exist in the `shared` module and can be imported across other services. | **Completed** | - |

---

### 2. Database Schema & ORM Setup (Ingestion Store)

| Change ID | Task / Phase | Tech Used | L5 Architecture & Design Notes | Verification Goal (What to Test / Verify) | Status | Commit Link |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CHG-004** | DB Storage Layers Setup | Spring Boot, JPA, PostgreSQL | Setting up initial database connectivity frameworks for persistence. | Run a clean compile and boot any application module without DB connection errors. | **Pending** | - |
| **CHG-005** | Create `schema.sql` | PostgreSQL, SQL DDL | Optimizing schema normalization, primary keys, and index selections for write-heavy tombstone records. | Run Postgres container, execute the schema DDL, and verify all tables (`documents`, `fragments`, `tombstones`) exist. | **Pending** | - |
| **CHG-006** | Write JPA Entity Models | Java, Hibernate | Mapping complex objects to relational schema. Optimizing lazy loading vs. eager fetching. | Hibernate startup validation successfully runs without mapping errors (e.g., relationship exceptions). | **Pending** | - |
| **CHG-007** | Configure Datasources | HikariCP, YAML | Connection pool tuning (sizes, idle timeouts) to prevent thread starvation under high transactional QPS. | Boot application and verify that connection counts stay within the configured Hikari pool limits during stress tests. | **Pending** | - |
| **CHG-008** | DB Integration Tests | JUnit 5, Testcontainers | Testing transactional isolation levels and rollback behaviors against a real Postgres container. | Run `mvn test` in the database integration module and get a green test run with zero connection leaks. | **Pending** | - |

---

### 3. Crawl & Ingestion Service

| Change ID | Task / Phase | Tech Used | L5 Architecture & Design Notes | Verification Goal (What to Test / Verify) | Status | Commit Link |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CHG-009** | PDF Text Extraction | Apache PDFBox, URLConnection | Resilient file downloading, HTTP User-Agent routing, timeouts, and text cleanup. | Pass a URL to the parser and verify clean text output with zero memory leaks. | **Completed** | - |
| **CHG-010** | Ingestion Thread Pools | TaskExecutors | Tuning core/max pools to run heavy I/O tasks asynchronously without blocking web threads. | Trigger crawling and monitor thread names to ensure task execution occurs in worker pools. | **Completed** | - |
| **CHG-011** | Hierarchical Traversal Loop | BFS/DFS Java | Algorithmic graph traversal. Establishing depth limits to avoid stack overflow and resource exhaustion. | Run crawling on a mock site tree and verify it parses nodes up to max-depth without getting trapped in cycles. | **Pending** | - |
| **CHG-012** | Graph Cycle & Size Checks | Redis Bloom Filter / Set | Using space-efficient visited sets (Bloom filters) for O(1) membership checks; size enforcement. | Feed a cyclic structure and a >10MB file; verify it skips already-visited nodes and rejects large payloads instantly. | **Pending** | - |
| **CHG-013** | Kafka Raw Ingest Queue | Spring Kafka | Tuning producers for high-throughput (batch sizes, LZ4 compression, write confirmations `acks=all`). | Publish mock documents and verify payload strings successfully appear on the `docs.crawl` topic via Kafdrop. | **Pending** | - |

---

### 4. SimHash Deduplication Service

| Change ID | Task / Phase | Tech Used | L5 Architecture & Design Notes | Verification Goal (What to Test / Verify) | Status | Commit Link |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CHG-014** | Deduplication Strategy | Java, Spring Boot | Near-duplicate detection at scale. Hashing vs. semantic similarity trade-offs. | Initialize service routing pipeline to process content fingerprints. | **Pending** | - |
| **CHG-015** | Implement SimHash | Java, MurmurHash3 | Converting variable-length token lists into fixed-length 64-bit signatures. | Run unit tests calculating hashes for two matching vs. slightly different strings; confirm proper bit distribution. | **Pending** | - |
| **CHG-016** | Hamming Distance Lookups | Bitwise Operations | Bitwise XOR and `Long.bitCount` (popcount) lookup performance optimization in memory-mapped tables. | Assert that distance checks between hashes return exact matching bit differences in sub-millisecond ranges. | **Pending** | - |
| **CHG-017** | Workflow Skip Policies | Java | Decoupling business policy (skip downstream crawl if distance <= 8 bits) from data capture operations. | Process a duplicate doc; verify that downstream Kafka event publishing and graph parsing are completely bypassed. | **Pending** | - |
| **CHG-018** | Expose Dedup APIs | Spring Boot REST | API contract design, thread-safe request routing, and error response validation. | Query `/dedup/process` with content and assert response contains the 64-bit fingerprint and duplicate verdict. | **Pending** | - |

---

### 5. Document Service & Fragmentation Ingestion

| Change ID | Task / Phase | Tech Used | L5 Architecture & Design Notes | Verification Goal (What to Test / Verify) | Status | Commit Link |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CHG-019** | Route Priority Consumers | Spring Kafka | Priority Queues: Setting up consumer groups for Subscription (High), Traffic (Medium), and Registry (Low). | Verify priority routing configuration maps to corresponding Kafka topic queues. | **Completed** | - |
| **CHG-020** | Ingestion Thread Isolation | Spring TaskExecutors | Implementing bulkheads to isolate threads, preventing low-priority tasks from consuming all server resources. | Monitor execution and verify that a flood of Low-priority tasks doesn't starve the High-priority pool. | **Completed** | - |
| **CHG-021** | Ingestion Batch Buffers | Thread-Safe Lists | Buffer Optimization: In-memory batch accumulation to speed up bulk inserts to PostgreSQL. | Verify that buffering triggers a database flush only when the batch limit (e.g., 100) or timeout is reached. | **Completed** | - |
| **CHG-022** | Traffic Metrics Scheduler | Spring Scheduler | Distributed scheduling. Preventing double-triggering across multiple scale instances of the service. | Trigger scheduler and verify that lock-records prevent other threads/instances from running duplicate jobs. | **Pending** | - |
| **CHG-023** | Gap-Based Sequencing | Java, SQL | Utilizing sparse sequence gaps (e.g., indices 100, 200, 300) to support instant middle-inserts without index re-writes. | Insert a fragment in the middle of a document; verify its sequence ID is updated to 150 without touching siblings. | **Pending** | - |
| **CHG-024** | Segment Rebalancing | Java | Algorithmic rebalance trigger when sparse index gaps are fully exhausted, minimizing locking and surges. | Perform consecutive inserts until space is exhausted (e.g., inserting between 150 and 151); assert re-spacing runs. | **Pending** | - |
| **CHG-025** | Storage Stage Transitions | Spring Transactions | ACID boundaries. Moving fragments cleanly from staging to publishing without leaving orphan records. | Force a write failure on the last segment; verify that database state rollbacks cleanly to original state. | **Pending** | - |

---

### 6. Inverted Index Pipeline Construction

| Change ID | Task / Phase | Tech Used | L5 Architecture & Design Notes | Verification Goal (What to Test / Verify) | Status | Commit Link |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CHG-026** | Elasticsearch Schema Init | ES Java Client | Automating schema mapping definitions (keyword vs text fields) dynamically on application start. | Boot the service; verify index creation and schema mappings in Elasticsearch console (`GET /search-index/_mapping`). | **Completed** | - |
| **CHG-027** | Tokenization & Stop-Words | Regex / NLP Libraries | Performance-sensitive string manipulation. Pruning noise words to optimize search index size. | Process raw query inputs and assert stop-words (e.g., "the", "a", "is") are completely stripped from parsed index lists. | **Pending** | - |
| **CHG-028** | Multi-Shard Index Routing | Java, Modulo Hash | Sharding partition routing strategies to prevent writing hot spots on popular keywords. | Map various tokens to shard routes; assert routing formula distributes load uniformly across index targets. | **Pending** | - |
| **CHG-029** | Eventually Consistent Runs | Spring Scheduled | Eventual consistency modeling. Batching updates to maximize disk write/merge efficiency. | Index documents and verify that data updates become queryable inside search indices within the 5s target window. | **Pending** | - |
| **CHG-030** | Tombstone Delete Updates | JPA, Postgres | soft-deletes via database tombstones to prevent random disk sector writes on search segments. | Request a deletion and query the database; verify the document row remains but a deletion marker is written. | **Pending** | - |
| **CHG-031** | In-Memory Delete Bitsets | Roaring Bitmap / BitSet | Hardware-optimized bitwise AND/OR filters to ignore deleted documents instantly during query phases. | Mark a document as deleted; verify that its index ID matches the in-memory delete bitmask instantly. | **Pending** | - |

---

### 7. Query & Serving Service

| Change ID | Task / Phase | Tech Used | L5 Architecture & Design Notes | Verification Goal (What to Test / Verify) | Status | Commit Link |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CHG-032** | Query REST Endpoint | Spring Boot Web | Throttling requests at the edge, sanitizing input vectors, and path validations. | Send a search query containing malicious payloads or stop-words; verify sanitization runs and filters payload. | **Pending** | - |
| **CHG-033** | Shard Postings Retrieval | Java I/O Streams | Optimizing memory-mapped files (mmap) or bulk HTTP payloads to fetch raw keyword postings. | Fetch posting list for a term; verify list contains document references and locations in sorted sequence order. | **Pending** | - |
| **CHG-034** | Bitset Delete Filtering | Java BitSet | Filtering arrays at the memory layer before invoking CPU-heavy ranking calculations. | Search for a keyword in a document marked for deletion; confirm search results omit it before computing ranking. | **Pending** | - |
| **CHG-035** | TF-IDF / BM25 Ranking | Math Java | Normalizing scores based on matching fragment length and global word distribution frequencies. | Execute search and assert matching items are ranked in order of term frequency/inverse document frequency scores. | **Pending** | - |
| **CHG-036** | Snippet Highlight Gen | String Sliding Window | CPU optimization: sliding window algorithms for context matching, token highlighting, and escaping safety. | Query text and verify returned snippet highlights matched terms with `<em>` tags within a sliding window. | **Pending** | - |
| **CHG-037** | Deep-Link Navigation API | Spring Boot Web | Offset-based fragment mapping and deep link routing back to full documents. | Fetch fragment offset and verify request points user to the exact deep link section of the matching document. | **Pending** | - |

---

### 8. Testing & Performance Optimization

| Change ID | Task / Phase | Tech Used | L5 Architecture & Design Notes | Verification Goal (What to Test / Verify) | Status | Commit Link |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CHG-038** | E2E Integration Flow | Docker Compose | Verifying asynchronous pipeline message states (Kafka payloads matching Elasticsearch hits). | Run shell script to load a PDF and verify search index returns its exact snippet via `/query/search`. | **Pending** | - |
| **CHG-039** | Latency Validation Tests | wrk / Locust | Simulating concurrency. Analyzing histograms (p50, p95, p99) to locate pipeline performance caps. | Run stress scripts against search API with 1M documents; assert p95 latency stays under 1000ms. | **Pending** | - |
| **CHG-040** | GC and Memory Profiling | JProfiler / VisualVM | JVM garbage collection tuning. Optimizing primitives arrays vs objects to keep allocation rates clean. | Trace heap usage under continuous index pressure; assert JVM GC CPU usage accounts for less than 5% total load. | **Pending** | - |
