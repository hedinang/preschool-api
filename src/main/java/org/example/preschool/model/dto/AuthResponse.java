package org.example.preschool.model.dto;

import org.example.preschool.model.entity.User;

public record AuthResponse(String userId, String username, String role, String token) {

    public static AuthResponse from(User user) {
        return new AuthResponse(
                user.getUserId(),
                user.getUsername(),
                user.getRole().name(),
                user.getAccessToken()
        );
    }
}
