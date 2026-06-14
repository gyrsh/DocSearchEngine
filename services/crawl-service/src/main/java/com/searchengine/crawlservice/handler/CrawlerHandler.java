package com.searchengine.crawlservice.handler;

import com.searchengine.crawlservice.model.CrawlerEventDto;
import org.springframework.stereotype.Component;

@Component
public class CrawlerHandler {

    private final PdfParser pdfParser;

    public CrawlerHandler(PdfParser pdfParser) {
        this.pdfParser = pdfParser;
    }

    public void handle(CrawlerEventDto crawlerEventDto) {
        String textToSave = pdfParser.handle(crawlerEventDto.getDocURL());
        // TODO : Implement the crawling logic
    }

}
