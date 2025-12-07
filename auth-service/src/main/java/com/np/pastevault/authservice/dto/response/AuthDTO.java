package com.np.pastevault.authservice.dto.response;

public record AuthDTO(String username,
                      String token,
                      String role) {
}
