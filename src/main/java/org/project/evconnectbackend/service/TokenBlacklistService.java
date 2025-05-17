package org.project.evconnectbackend.service;

import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.model.BlacklistedToken;
import org.project.evconnectbackend.repository.BlacklistedTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Transactional
    public void blacklistToken(String token, LocalDateTime expiresAt) {
        if (!blacklistedTokenRepository.existsByToken(token)) {
            BlacklistedToken blacklistedToken = new BlacklistedToken();
            blacklistedToken.setToken(token);
            blacklistedToken.setExpiresAt(expiresAt);
            blacklistedTokenRepository.save(blacklistedToken);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        try {
            blacklistedTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        } catch (Exception e) {
            // Log the error but don't throw it to prevent the scheduled task from failing
            System.err.println("Error cleaning up expired tokens: " + e.getMessage());
        }
    }
} 