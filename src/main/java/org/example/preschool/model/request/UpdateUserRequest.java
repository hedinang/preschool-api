package org.example.preschool.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.preschool.util.constant.RoleType;

public record UpdateUserRequest(
        @NotNull RoleType role,
        @NotBlank String name,
        String title,
        String classroom,
        @Size(min = 6, max = 100) String password,
        String phone,
        String phone2,
        String address,
        String gender
) {
}
