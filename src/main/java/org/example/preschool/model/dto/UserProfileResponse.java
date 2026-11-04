package org.example.preschool.model.dto;

import org.example.preschool.model.entity.User;

public record UserProfileResponse(String userId, String username, String role) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(user.getUserId(), user.getUsername(), user.getRole().name());
    }
}
