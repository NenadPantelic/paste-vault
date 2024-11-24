package com.pastevault.pastevault.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class StorageNode {

    @Builder.Default
    private StorageType storageType = StorageType.LINK;

    // url
    // text
    // filePath - unsupported as of now
    private String value;
}
