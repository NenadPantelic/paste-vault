package com.pastevault.usermanager.service.impl;

import com.pastevault.common.exception.ApiException;
import com.pastevault.common.exception.ErrorReport;
import com.pastevault.usermanager.dto.request.EditUser;
import com.pastevault.usermanager.dto.request.NewUser;
import com.pastevault.usermanager.dto.response.UserRepresentation;
import com.pastevault.usermanager.mapper.UserMapper;
import com.pastevault.usermanager.model.Role;
import com.pastevault.usermanager.model.User;
import com.pastevault.usermanager.repository.UserRepository;
import com.pastevault.usermanager.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserRepresentation createUser(NewUser newUser) {
        log.info("Create a new user: {}", newUser);

        User user = User.builder()
                .firstName(newUser.firstName())
                .lastName(newUser.lastName())
                .email(newUser.email())
                .username(newUser.username())
                .password(passwordEncoder.encode(newUser.password()))
                .role(Role.fromValue(newUser.role()))
                .build();

        user = userRepository.doSave(user);
        return UserMapper.mapToDTO(user);
    }

    @Override
    public UserRepresentation getUser(int userId) {
        log.info("Get user with id {}", userId);
        User user = userRepository.findOrNotFound(userId);
        return UserMapper.mapToDTO(user);
    }

    @Override
    public List<UserRepresentation> listUsers(int page, int size) {
        log.info("Listing users: page = {}, size = {}", page, size);
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "createdAt")
        );
        List<User> users = userRepository.findAll(pageRequest).toList();
        return UserMapper.mapToDTOList(users);
    }

    @Override
    public UserRepresentation updateUser(int userId, EditUser editUser) {
        log.info("Updating a user with id {}", userId);
        User user = userRepository.findOrNotFound(userId);
        user.setFirstName(editUser.firstName());
        user.setLastName(editUser.lastName());
        user = userRepository.doSave(user);
        return UserMapper.mapToDTO(user);
    }

    @Override
    public void deleteUser(int userId) {
        log.info("Deleting a user with id {}", userId);

        User user = userRepository.findOrNotFound(userId);
        // TODO: prohibit the self-deletion
        if (user.isAdmin()) {
            String errMessage = String.format("Unable to delete the admin user with id %d", userId);
            log.warn(errMessage);
            throw new ApiException(ErrorReport.FORBIDDEN.withErrors(errMessage));
        }

        userRepository.delete(user);
    }
}
