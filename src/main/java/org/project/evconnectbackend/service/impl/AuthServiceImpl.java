package org.project.evconnectbackend.service.impl;

import lombok.RequiredArgsConstructor;

import org.project.evconnectbackend.dto.auth.JwtAuthResponse;
import org.project.evconnectbackend.dto.auth.LoginRequest;
import org.project.evconnectbackend.dto.auth.RegisterRequest;
import org.project.evconnectbackend.model.User;
import org.project.evconnectbackend.model.UserType;
import org.project.evconnectbackend.repository.UserRepository;
import org.project.evconnectbackend.security.JwtTokenProvider;
import org.project.evconnectbackend.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    public JwtAuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already taken");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserType(UserType.valueOf(request.getUserType()));
        user.setCurrentRole(UserType.valueOf(request.getUserType()));

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return new JwtAuthResponse(jwt, "Bearer", user.getUserType().toString(), user.getCurrentRole().toString());
    }

    @Override
    public JwtAuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new JwtAuthResponse(jwt, "Bearer", user.getUserType().toString(), user.getCurrentRole().toString());
    }
} 