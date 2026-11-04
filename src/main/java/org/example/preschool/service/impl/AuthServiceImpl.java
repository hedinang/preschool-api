package org.example.preschool.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.AuthResponse;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.dto.UserProfileResponse;
import org.example.preschool.model.entity.User;
import org.example.preschool.model.request.LoginRequest;
import org.example.preschool.repository.UserRepository;
import org.example.preschool.service.AuthService;
import org.example.preschool.util.constant.ErrorCode;
import org.example.preschool.util.exception.ServiceException;
import org.example.preschool.util.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.username(),
                        request.password()
                )
        );
        User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        if (user.getRole() == null) {
            throw new ServiceException(ErrorCode.E403.code(), "User role is not assigned");
        }
        user.setAccessToken(tokenProvider.generateToken(user));
        return AuthResponse.from(userRepository.save(user));
    }

    @Override
    public UserProfileResponse getProfile(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.E404.code(), "User not found"));
        if (user.getRole() == null) {
            throw new ServiceException(ErrorCode.E403.code(), "User role is not assigned");
        }
        return UserProfileResponse.from(user);
    }

    @Override
    public void logout(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.E404.code(), "User not found"));
        user.setAccessToken(null);
        userRepository.save(user);
    }
}
