package org.example.preschool.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.AuthResponse;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.dto.UserProfileResponse;
import org.example.preschool.model.request.LoginRequest;
import org.example.preschool.service.AuthService;
import org.example.preschool.util.response.BaseResponse;
import org.example.preschool.util.response.Response;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public BaseResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return Response.toData(authService.login(request));
    }

    @GetMapping("/me")
    public BaseResponse<UserProfileResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return Response.toData(authService.getProfile(userDetails.getUser().getUserId()));
    }

    @PostMapping("/logout")
    public BaseResponse<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getUser().getUserId());
        return Response.toData(null, "Logged out successfully");
    }
}
