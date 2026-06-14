package com.searchengine.indexingservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ElasticsearchSearchIndexService implements SearchIndexService {

    private final ElasticsearchClient elasticsearchClient;
    private final String indexName;

    public ElasticsearchSearchIndexService(
            ElasticsearchClient elasticsearchClient,
            @Value("${elasticsearch.index-name}") String indexName) {
        this.elasticsearchClient = elasticsearchClient;
        this.indexName = indexName;
    }

    @Override
    public void ensureIndex() throws IOException {
        boolean exists = elasticsearchClient.indices()
                .exists(request -> request.index(indexName))
                .value();

        if (exists) {
            return;
        }

        elasticsearchClient.indices().create(request -> request
                .index(indexName)
                .mappings(mapping -> mapping
                        .properties("documentId", property -> property.keyword(keyword -> keyword))
                        .properties("title", property -> property.text(text -> text))
                        .properties("pageNumber", property -> property.integer(integer -> integer))
                        .properties("chunkNumber", property -> property.integer(integer -> integer))
                        .properties("content", property -> property.text(text -> text))
                        .properties("extractedAt", property -> property.date(date -> date))
                )
        );
    }

}
