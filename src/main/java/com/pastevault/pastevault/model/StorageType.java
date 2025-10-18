package com.pastevault.pastevault.model;

public enum StorageType {

    LINK,
    TEXT;

    public static StorageType convertIfPossible(String text) {
        try {
            return StorageType.valueOf(text);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
