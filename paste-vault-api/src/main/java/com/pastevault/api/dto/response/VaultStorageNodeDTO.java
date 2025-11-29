package com.pastevault.api.dto.response;

import com.pastevault.api.model.StorageType;
import lombok.Builder;

@Builder
public record VaultStorageNodeDTO(StorageType storageType,
                                  String value) {
}
