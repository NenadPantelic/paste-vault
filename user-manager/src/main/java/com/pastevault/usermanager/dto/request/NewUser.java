package com.pastevault.usermanager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewUser(@NotBlank @Size(min = 1, max = 64) String firstName,
                      @NotBlank @Size(min = 1, max = 64) String lastName,
                      @NotBlank @Email String email,
                      @NotBlank @Size(min = 8, max = 32) String username,
                      @NotBlank @Size(min = 8, max = 64) String password,
                      @NotBlank String role) {

    @Override
    public String toString() {
        return "NewUser{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
