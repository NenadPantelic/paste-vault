package com.pastevault.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


// url
// text
// filePath - unsupported as of now
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StorageNode {

    @Builder.Default
    private StorageType storageType = StorageType.LINK;

}
