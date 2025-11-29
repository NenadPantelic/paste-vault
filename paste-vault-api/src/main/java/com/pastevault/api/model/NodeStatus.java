package com.pastevault.api.model;

public enum NodeStatus {

    // being imported, updated or deleted
    UNAVAILABLE,
    // successfully stored in both databases
    READY
}
