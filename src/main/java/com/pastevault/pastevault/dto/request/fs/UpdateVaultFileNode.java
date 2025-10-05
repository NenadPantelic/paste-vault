package com.pastevault.pastevault.dto.request.fs;

import com.pastevault.pastevault.dto.request.content.PasteableItem;
import jakarta.validation.constraints.NotBlank;

public record UpdateVaultFileNode(@NotBlank String name,
                                  PasteableItem storageNode) {
}
