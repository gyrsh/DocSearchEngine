# Search Engine for Nested Documents

Java multi-module project scaffold for the Search Engine design.

## Modules

- `shared` - shared types, contracts, common utilities, tracing helpers
- `crawl-service` - document crawling and ingestion
- `dedup-service` - duplicate detection and simhash management
- `indexing-service` - inverted index building and update processing
- `query-service` - user query handling, ranking, snippet generation
- `document-service` - document retrieval and fragment navigation

## Architecture

This project is organized as a Maven multi-module Java application using Spring Boot for each service.

## Workspace Structure

- `services/` - service-specific implementation modules
- `shared/` - shared code and contracts
- `infra/` - deployment and infrastructure helpers
- `scripts/` - automation and helper scripts
- `docs/` - design documentation

## Build

```bash
mvn clean install
```

## Run

Each service can be started independently from its module directory using:

```bash
mvn spring-boot:run
```
