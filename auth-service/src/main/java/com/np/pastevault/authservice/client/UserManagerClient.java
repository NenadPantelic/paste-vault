package com.np.pastevault.authservice.client;

import com.np.pastevault.authservice.dto.response.IdentityInfo;

public interface UserManagerClient {

    IdentityInfo fetchUserByCredentials(String username, String password);
}
