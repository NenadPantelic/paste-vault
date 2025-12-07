package com.pastevault.common.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserByCredentialsQuery(@NotBlank String username,
                                     @NotBlank String password) {
}
