package com.searchengine.crawlservice.service;

import com.searchengine.crawlservice.model.CrawlerEventDto;
import java.util.List;

public interface CrawlService {
    void processSubscriptionEvent(CrawlerEventDto eventDto);

    void processTrafficEventBatch(List<CrawlerEventDto> events);

    void processRegistryEvent(CrawlerEventDto eventDto);
}
