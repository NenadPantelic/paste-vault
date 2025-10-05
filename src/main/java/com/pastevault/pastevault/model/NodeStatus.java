package com.pastevault.pastevault.model;

public enum NodeStatus {

    // being imported, updated or deleted
    UNAVAILABLE,
    // successfully stored in both databases
    READY
}
