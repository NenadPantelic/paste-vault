package com.pastevault.usermanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditUser(@NotBlank @Size(min = 1, max = 64) String firstName,
                       @NotBlank @Size(min = 1, max = 64) String lastName) {
}
