package com.pastevault.usermanager.controller;


import com.pastevault.common.dto.request.UserByCredentialsQuery;
import com.pastevault.usermanager.dto.response.UserDTO;
import com.pastevault.usermanager.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/internal")
public class UserInternalController {

    private final UserService userService;

    public UserInternalController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDTO getUserByUsernameAndPassword(@Valid @RequestBody UserByCredentialsQuery userByCredentialsQuery) {
        log.info("Received a request to find a user by its credentials");
        return userService.getUserByCredentials(userByCredentialsQuery);
    }
}
