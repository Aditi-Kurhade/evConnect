package org.project.evconnectbackend.service.impl;

import lombok.RequiredArgsConstructor;

import org.project.evconnectbackend.dto.user.ChangePasswordRequest;
import org.project.evconnectbackend.dto.user.UpdateProfileRequest;
import org.project.evconnectbackend.model.User;
import org.project.evconnectbackend.model.UserType;
import org.project.evconnectbackend.repository.UserRepository;
import org.project.evconnectbackend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Object getUserProfile(String email) {
        System.out.println(userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Object updateUserProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUserType(UserType.valueOf(request.getUserType()));

        return userRepository.save(user);
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update to new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public Object switchUserRole(String email, String role) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserType newRole = UserType.valueOf(role);
        System.out.println(user.getUserType() + " " + newRole);
        if (user.getCurrentRole() == newRole) {
            throw new RuntimeException("User cannot switch to this role");
        }

        user.setCurrentRole(newRole);
        return userRepository.save(user);
    }
} 