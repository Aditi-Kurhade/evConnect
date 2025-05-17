package org.project.evconnectbackend.config;

import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.service.TokenBlacklistService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final TokenBlacklistService tokenBlacklistService;

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        tokenBlacklistService.cleanupExpiredTokens();
    }
} 