package org.example.preschool.service.impl;

import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.User;
import org.example.preschool.model.request.LoginRequest;
import org.example.preschool.repository.UserRepository;
import org.example.preschool.util.constant.RoleType;
import org.example.preschool.util.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    @Test
    void loginAuthenticatesAndPersistsTheIssuedToken() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        UserRepository userRepository = mock(UserRepository.class);
        AuthServiceImpl authService = new AuthServiceImpl(
                authenticationManager,
                tokenProvider,
                userRepository
        );

        User user = new User();
        user.setUserId("user-1");
        user.setUsername("parent");
        user.setRole(RoleType.CUSTOMER);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user));
        when(tokenProvider.generateToken(user)).thenReturn("signed-token");
        when(userRepository.save(user)).thenReturn(user);

        var response = authService.login(new LoginRequest("parent", "password"));

        assertEquals("user-1", response.userId());
        assertEquals("CUSTOMER", response.role());
        assertEquals("signed-token", response.token());
        verify(userRepository).save(user);
    }
}
