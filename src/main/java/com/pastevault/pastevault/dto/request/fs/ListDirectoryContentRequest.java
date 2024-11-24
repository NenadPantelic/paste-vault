package com.pastevault.pastevault.dto.request.fs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ListDirectoryContentRequest(@NotBlank String parentPath,
                                          @Min(0) int page,
                                          @Min(0) @Max(250) int size) {
}
