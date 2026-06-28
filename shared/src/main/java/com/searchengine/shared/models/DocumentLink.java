package com.searchengine.shared.models;

import java.time.Instant;

public class DocumentLink {

    private String id;

    // Document where the link was found
    private String sourceDocumentId;

    // URL that was discovered
    private String targetUrl;

    // Visible text associated with the link
    private String anchorText;

    // nofollow, sponsored, etc.
    private String rel;

    private Instant discoveredAt;
}
