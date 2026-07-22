package com.liquor.liquor.service;

import com.liquor.liquor.dto.AuthRequest;
import com.liquor.liquor.dto.AuthResponse;
import com.liquor.liquor.dto.UserRequest;

public interface AuthService {

    AuthResponse signup(UserRequest request);

    AuthResponse login(AuthRequest request);
}
