package com.pastevault.api.context;

public record UserContext(String userId,
                          String username,
                          Role role) {
}
