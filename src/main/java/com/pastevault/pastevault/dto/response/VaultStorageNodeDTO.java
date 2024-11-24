package com.pastevault.pastevault.dto.response;

import com.pastevault.pastevault.model.StorageType;
import lombok.Builder;

@Builder
public record VaultStorageNodeDTO(StorageType storageType,
                                  String value) {
}
