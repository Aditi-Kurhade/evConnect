package org.project.evconnectbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.evconnectbackend.dto.user.ChangePasswordRequest;
import org.project.evconnectbackend.dto.user.UpdateProfileRequest;
import org.project.evconnectbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserProfile(userDetails.getUsername()));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateUserProfile(userDetails.getUsername(), request));
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/switch-role")
    public ResponseEntity<?> switchRole(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String role) {
        return ResponseEntity.ok(userService.switchUserRole(userDetails.getUsername(), role));
    }
} 