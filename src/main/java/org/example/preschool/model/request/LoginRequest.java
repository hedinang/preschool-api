package org.example.preschool.model.request;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonAlias;

public record LoginRequest(
        @NotBlank @JsonAlias("email") String username,
        @NotBlank String password
) {
}
