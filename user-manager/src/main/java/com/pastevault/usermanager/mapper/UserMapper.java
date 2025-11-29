package com.pastevault.usermanager.mapper;

import com.pastevault.usermanager.dto.response.UserRepresentation;
import com.pastevault.usermanager.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserRepresentation mapToDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserRepresentation.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public static List<UserRepresentation> mapToDTOList(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(UserMapper::mapToDTO)
                .collect(Collectors.toList());
    }
}
