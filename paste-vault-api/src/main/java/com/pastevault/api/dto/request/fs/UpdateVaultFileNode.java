package com.pastevault.api.dto.request.fs;

import com.pastevault.api.dto.request.content.PasteableItem;
import jakarta.validation.constraints.NotBlank;

public record UpdateVaultFileNode(@NotBlank String name,
                                  PasteableItem storageNode) {
}
