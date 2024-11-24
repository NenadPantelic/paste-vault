package com.pastevault.pastevault.dto.request.fs;

import com.pastevault.pastevault.dto.request.content.PastableItem;
import jakarta.validation.constraints.NotBlank;

public record UpdateVaultFileNode(@NotBlank String name,
                                  PastableItem storageNode) {
}
