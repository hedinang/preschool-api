package org.example.preschool.model.dto;

import org.example.preschool.model.entity.User;
import org.example.preschool.util.constant.RoleType;

public record ManagedUserResponse(
        String id,
        String userId,
        String username,
        RoleType role,
        String name,
        String title,
        String classroom,
        String phone,
        String phone2,
        String address,
        String gender,
        Long sequence,
        String status,
        String createdAt
) {
    public static ManagedUserResponse from(User user) {
        return new ManagedUserResponse(
                user.getId(),
                user.getUserId(),
                user.getUsername(),
                user.getRole() == RoleType.CUSTOMER ? RoleType.PARENT : user.getRole(),
                user.getDisplayName() == null ? user.getUsername() : user.getDisplayName(),
                user.getTitle(),
                user.getClassroom(),
                user.getPhone(),
                user.getPhone2(),
                user.getAddress(),
                user.getGender(),
                user.getSequence(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
