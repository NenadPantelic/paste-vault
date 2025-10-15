package com.pastevault.pastevault.context;

public record UserContext(String userId,
                          String username,
                          Role role) {
}
