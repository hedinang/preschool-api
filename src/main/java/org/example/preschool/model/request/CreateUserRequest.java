package org.example.preschool.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.preschool.util.constant.RoleType;

public record CreateUserRequest(
        @NotBlank @Email String username,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotNull RoleType role,
        @NotBlank String name,
        String title,
        String classroom,
        String phone,
        String phone2,
        String address,
        String gender
) {
}
