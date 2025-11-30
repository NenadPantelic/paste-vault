package com.pastevault.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record VaultFileNodeDTO(String id,
                               String parentPath,
                               String name,
                               String type,
                               List<String> tags,
                               VaultStorageNodeDTO storage) {
}
