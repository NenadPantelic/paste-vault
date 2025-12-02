package com.pastevault.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NewTag(@NotBlank String name) {
}
