package com.searchengine.indexingservice.service;

import java.io.IOException;
import java.util.List;

public interface SearchIndexService {
    void ensureIndex() throws IOException;
}
