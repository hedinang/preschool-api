package org.example.preschool.service;

import org.example.preschool.model.dto.AuthResponse;
import org.example.preschool.model.dto.UserProfileResponse;
import org.example.preschool.model.request.LoginRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    UserProfileResponse getProfile(String userId);

    void logout(String userId);
}
