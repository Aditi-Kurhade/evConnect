package org.project.evconnectbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.dto.auth.JwtAuthResponse;
import org.project.evconnectbackend.dto.auth.LoginRequest;
import org.project.evconnectbackend.dto.auth.RegisterRequest;
import org.project.evconnectbackend.security.JwtTokenProvider;
import org.project.evconnectbackend.service.AuthService;
import org.project.evconnectbackend.service.TokenBlacklistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping({"/auth", "/api/auth"})
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            // Get token expiration date
            Date expirationDate = tokenProvider.getExpirationDateFromToken(token);
            
            // Blacklist the token
            tokenBlacklistService.blacklistToken(token, expirationDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.ok().build(); // Still return OK to client to allow it to clear local state
        }
    }
} 