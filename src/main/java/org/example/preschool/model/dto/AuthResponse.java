package org.example.preschool.model.dto;

import org.example.preschool.model.entity.User;
import org.example.preschool.util.constant.RoleType;

public record AuthResponse(
        String userId,
        String username,
        String role,
        String token,
        String portalRole,
        String name,
        String title,
        String email,
        String classroom,
        String studentId,
        String avatar,
        String color
) {

    public static AuthResponse from(User user) {
        return new AuthResponse(
                user.getUserId(),
                user.getUsername(),
                user.getRole().name(),
                user.getAccessToken(),
                toPortalRole(user.getRole()),
                user.getDisplayName() == null ? user.getUsername() : user.getDisplayName(),
                user.getTitle(),
                user.getUsername(),
                user.getClassroom(),
                user.getStudentId(),
                user.getAvatar(),
                user.getColor()
        );
    }

    private static String toPortalRole(RoleType role) {
        return switch (role) {
            case ADMIN, SUPER_ADMIN -> "admin";
            case TEACHER -> "teacher";
            case PARENT, CUSTOMER -> "parent";
            case STUDENT -> "student";
        };
    }
}
