package com.pastevault.pastevault.model;

public enum VaultNodeType {

    FILE,
    DIR;

    public static VaultNodeType convertIfPossible(String text) {
        try {
            return VaultNodeType.valueOf(text);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
