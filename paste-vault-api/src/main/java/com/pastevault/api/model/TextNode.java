package com.pastevault.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TextNode extends StorageNode {

    private final String text;

    public TextNode(StorageType storageType, String text) {
        super(storageType);
        this.text = text;
    }
}
