package org.project.evconnectbackend.service;


import org.project.evconnectbackend.dto.user.ChangePasswordRequest;
import org.project.evconnectbackend.dto.user.UpdateProfileRequest;

public interface UserService {
    Object getUserProfile(String email);
    Object updateUserProfile(String email, UpdateProfileRequest request);
    Object switchUserRole(String email, String role);
    void changePassword(String email, ChangePasswordRequest request);
} 