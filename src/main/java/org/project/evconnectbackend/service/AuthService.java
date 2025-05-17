package org.project.evconnectbackend.service;

import org.project.evconnectbackend.dto.auth.JwtAuthResponse;
import org.project.evconnectbackend.dto.auth.LoginRequest;
import org.project.evconnectbackend.dto.auth.RegisterRequest;

public interface AuthService {
    JwtAuthResponse register(RegisterRequest request);
    JwtAuthResponse login(LoginRequest request);
} 