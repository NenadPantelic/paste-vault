package com.pastevault.usermanager.service;

import com.pastevault.usermanager.dto.request.EditUser;
import com.pastevault.usermanager.dto.request.NewUser;
import com.pastevault.usermanager.dto.response.UserRepresentation;

import java.util.List;

public interface UserService {

    /**
     * Creates a new user.
     *
     * @param newUser a new user's details
     */
    UserRepresentation createUser(NewUser newUser);

    /**
     * Gets the user by the given id.
     * If the user does not exist, returns an appropriate error.
     *
     * @param userId an identifier of the user
     */
    UserRepresentation getUser(int userId);

    /**
     * Lists users with respect to the given paging parameters.
     *
     * @param page page number
     * @param size page size
     */
    List<UserRepresentation> listUsers(int page, int size);

    /**
     * Updates a user with the given id.
     *
     * @param userId  an identifier of the user
     * @param editUser property values to update the user
     */
    UserRepresentation updateUser(int userId, EditUser editUser);

    /**
     * Deletes a user with the given id.
     *
     * @param userId an identifier of the user
     */
    void deleteUser(int userId);
}
