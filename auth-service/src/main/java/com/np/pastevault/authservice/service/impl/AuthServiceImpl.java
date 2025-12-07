package com.np.pastevault.authservice.service.impl;

import com.np.pastevault.authservice.client.UserManagerClient;
import com.np.pastevault.authservice.dto.request.AuthRequest;
import com.np.pastevault.authservice.dto.response.AuthDTO;
import com.np.pastevault.authservice.dto.response.IdentityInfo;
import com.np.pastevault.authservice.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserManagerClient userManagerClient;

    public AuthServiceImpl(UserManagerClient userManagerClient) {
        this.userManagerClient = userManagerClient;
    }

    @Override
    public AuthDTO authenticate(AuthRequest authRequest) {
        // fetch the user - by username and hashed password
        IdentityInfo identity = userManagerClient.fetchUserByCredentials(
                authRequest.username(), authRequest.password()
        );
        // create a JWT
        return new AuthDTO(identity.username(), null, identity.role());
    }
}
