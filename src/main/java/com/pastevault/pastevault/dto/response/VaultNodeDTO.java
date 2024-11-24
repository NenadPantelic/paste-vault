package com.pastevault.pastevault.dto.response;

import lombok.Builder;

@Builder
public record VaultNodeDTO(String id,
                           String parentPath,
                           String name,
                           String type) {
}
