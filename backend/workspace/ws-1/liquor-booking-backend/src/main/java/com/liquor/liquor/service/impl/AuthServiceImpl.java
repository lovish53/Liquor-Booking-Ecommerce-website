package com.liquor.liquor.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.liquor.liquor.dto.AuthRequest;
import com.liquor.liquor.dto.AuthResponse;
import com.liquor.liquor.dto.UserRequest;
import com.liquor.liquor.dto.UserResponse;
import com.liquor.liquor.entity.UserEntity;
import com.liquor.liquor.security.JwtService;
import com.liquor.liquor.service.AuthService;
import com.liquor.liquor.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserService userService;

    private final ModelMapper modelMapper;

    @Override
    public AuthResponse signup(UserRequest request) {
        UserResponse userResponse = userService.registerUser(request);
        UserEntity user = userService.getUserEntity(userResponse.getId());

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        UserEntity user = userService.getUserByEmail(request.getEmail());

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .user(modelMapper.map(user, UserResponse.class))
                .build();
    }
}
