package com.example.task.service;

import com.example.task.dto.request.LoginRequest;
import com.example.task.dto.request.RegisterRequest;
import com.example.task.dto.request.UserRequest;
import com.example.task.dto.response.RegisterResponse;
import com.example.task.entity.User;
import com.example.task.mapper.Mapper;
import com.example.task.security.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtility jwtUtility;
    private final AuthenticationManager authenticationManager;


    public RegisterResponse register(RegisterRequest registerRequest) {
        UserRequest userRequest = Mapper.mapRegisterRequestToUserRequest(registerRequest);
        User user = userService.create(userRequest);
        String token = jwtUtility.generateAccessToken(user);
        return RegisterResponse
                .builder()
                .username(user.getUsername())
                .token(token)
                .build();
    }

    public RegisterResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);

        User user = (User) authentication.getPrincipal();
        String token = jwtUtility.generateAccessToken(Objects.requireNonNull(user));

        return RegisterResponse.builder()
                .username(user.getUsername())
                .token(token)
                .build();
    }
}
