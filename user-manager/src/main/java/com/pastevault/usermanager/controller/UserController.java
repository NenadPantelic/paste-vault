package com.pastevault.usermanager.controller;

import com.pastevault.usermanager.dto.request.EditUser;
import com.pastevault.usermanager.dto.request.NewUser;
import com.pastevault.usermanager.dto.response.UserRepresentation;
import com.pastevault.usermanager.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserRepresentation createUser(@RequestBody @Valid NewUser newUser) {
        log.info("Received a request to create new user...");
        return userService.createUser(newUser);
    }

    @GetMapping("/{userId}")
    public UserRepresentation getUser(@PathVariable(value = "userId") int userId) {
        log.info("Received a request to get a user...");
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserRepresentation> listUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "50") int size) {
        log.info("Received a request to list users...");
        return userService.listUsers(page, size);
    }

    @PutMapping("/{userId}")
    public UserRepresentation createUser(@PathVariable("userId") int userId, @RequestBody @Valid EditUser editUser) {
        log.info("Received a request to update a user...");
        return userService.updateUser(userId, editUser);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") int userId) {
        log.info("Received a request to delete a user...");
        userService.deleteUser(userId);
    }
}
