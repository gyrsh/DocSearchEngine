package com.searchengine.shared.models;

import java.time.Instant;

public class DocumentFragment {

    // PK
    private String fragmentId;

    // FK -> Document
    private String documentId;

    private Long fragmentOrder;

    private String content;

    private Long startOffset;
    private Long endOffset;

    private Long version;

    private Instant createdAt;
    private Instant updatedAt;
}