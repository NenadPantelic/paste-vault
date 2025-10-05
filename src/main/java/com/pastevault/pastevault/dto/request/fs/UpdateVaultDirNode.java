package com.pastevault.pastevault.dto.request.fs;

import jakarta.validation.constraints.NotBlank;

public record UpdateVaultDirNode(@NotBlank String name) {
}
