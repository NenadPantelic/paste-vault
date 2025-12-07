package com.np.pastevault.authservice.service;

import com.np.pastevault.authservice.dto.request.AuthRequest;
import com.np.pastevault.authservice.dto.response.AuthDTO;

public interface AuthService {

    AuthDTO authenticate(AuthRequest authRequest);
}
