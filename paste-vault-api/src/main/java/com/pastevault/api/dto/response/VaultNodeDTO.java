package com.pastevault.api.dto.response;

import lombok.Builder;

@Builder
public record VaultNodeDTO(String id,
                           String parentPath,
                           String name,
                           String type) {
}
