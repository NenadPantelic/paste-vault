package com.pastevault.pastevault.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LinkNode extends StorageNode {

    private final String url;

    public LinkNode(StorageType storageType, String url) {
        super(storageType);
        this.url = url;
    }
}
