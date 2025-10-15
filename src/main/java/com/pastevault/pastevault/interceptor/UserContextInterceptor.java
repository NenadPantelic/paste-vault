package com.pastevault.pastevault.interceptor;

import com.pastevault.apicommon.exception.ApiException;
import com.pastevault.apicommon.exception.ErrorReport;
import com.pastevault.pastevault.context.UserContextHolder;
import com.pastevault.pastevault.context.Role;
import com.pastevault.pastevault.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    private static final String USER_ID_HEADER = "X-user-id";
    private static final String USERNAME_HEADER = "X-username";
    private static final String USER_ROLE_HEADER = "X-user-role";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader(USER_ID_HEADER);
        String username = request.getHeader(USERNAME_HEADER);
        String userRole = request.getHeader(USER_ROLE_HEADER);

        if (isBlank(userId) || isBlank(username) || isBlank(userRole)) {
            log.error("All user context header values must not be blank. Values: userId = {}, username = {}, role = {}",
                    userId, username, userRole
            );
            throw new ApiException(ErrorReport.UNAUTHORIZED);
        }

        Role role = parseRole(userRole);
        if (role == null) {
            throw new ApiException(ErrorReport.UNAUTHORIZED);
        }

        UserContext context = new UserContext(userId, username, role);
        UserContextHolder.set(context);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        UserContextHolder.clear();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Role parseRole(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Unable to parse value {} to a valid Role type", value);
            return null;
        }
    }
}
