package com.pastevault.pastevault.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// url
// text
// filePath - unsupported as of now
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageNode {

    @Builder.Default
    private StorageType storageType = StorageType.LINK;

}
