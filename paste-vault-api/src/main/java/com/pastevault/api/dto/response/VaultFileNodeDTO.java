package com.pastevault.api.dto.response;

import lombok.Builder;

@Builder
public record VaultFileNodeDTO(String id,
                               String parentPath,
                               String name,
                               String type,
                               VaultStorageNodeDTO storage) {
}
