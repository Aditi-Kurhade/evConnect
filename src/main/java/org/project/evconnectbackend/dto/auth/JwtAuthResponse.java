package org.project.evconnectbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtAuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String userType;
    private String currentRole;
} 