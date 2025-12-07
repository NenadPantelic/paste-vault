package com.np.pastevault.authservice.controller;

import com.np.pastevault.authservice.dto.request.AuthRequest;
import com.np.pastevault.authservice.dto.response.AuthDTO;
import com.np.pastevault.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public AuthDTO authenticate(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Received a request to authenticate user");
        return authService.authenticate(authRequest);
    }
}

