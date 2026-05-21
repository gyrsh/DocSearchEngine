package com.searchengine.documentservice.model;

public enum DocumentType {
    SUBSCRIPTION("subscription", 1),      // Highest priority
    TRAFFIC("traffic", 2),                 // Medium priority
    REGISTRY("registry", 3);               // Lowest priority

    private final String type;
    private final int priority;

    DocumentType(String type, int priority) {
        this.type = type;
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public static DocumentType fromString(String type) {
        if (type == null) {
            return REGISTRY; // Default to lowest priority
        }
        for (DocumentType dt : DocumentType.values()) {
            if (dt.type.equalsIgnoreCase(type)) {
                return dt;
            }
        }
        return REGISTRY;
    }
}
