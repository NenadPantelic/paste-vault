package com.pastevault.pastevault.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class UserContextHolder {

    private static final ThreadLocal<UserContext> USER_CONTEXT_HOLDER = new ThreadLocal<>();

    public static void set(UserContext userContext) {
        USER_CONTEXT_HOLDER.set(userContext);
    }

    public static Optional<UserContext> get() {
        return Optional.ofNullable(USER_CONTEXT_HOLDER.get());
    }

    public static String getUserId() {
        return get().map(UserContext::userId).orElse(null);
    }

    public static String getUsername() {
        return get().map(UserContext::username).orElse(null);
    }

    public static Role getRole() {
        return get().map(UserContext::role).orElse(null);
    }

    public static void clear() {
        USER_CONTEXT_HOLDER.remove();
    }
}
