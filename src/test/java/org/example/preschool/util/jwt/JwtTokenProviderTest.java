package org.example.preschool.util.jwt;

import org.example.preschool.model.entity.User;
import org.example.preschool.util.constant.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    @Test
    void generatesAndValidatesTokenForUser() {
        JwtTokenProvider tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(
                tokenProvider,
                "jwtSecret",
                Base64.getEncoder().encodeToString(new byte[64])
        );
        ReflectionTestUtils.invokeMethod(tokenProvider, "initialize");

        User user = new User();
        user.setUserId("user-1");
        user.setRole(RoleType.ADMIN);
        String token = tokenProvider.generateToken(user);

        assertTrue(tokenProvider.validateToken(token));
        assertEquals("user-1", tokenProvider.getUserIdFromToken(token));
    }

    @Test
    void rejectsMissingJwtSecretWithActionableMessage() {
        JwtTokenProvider tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(tokenProvider, "initialize")
        );

        assertTrue(exception.getMessage().contains("JWT_SECRET is not configured"));
    }

    @Test
    void rejectsShortJwtSecret() {
        JwtTokenProvider tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(
                tokenProvider,
                "jwtSecret",
                Base64.getEncoder().encodeToString(new byte[32])
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(tokenProvider, "initialize")
        );

        assertTrue(exception.getMessage().contains("at least 64 bytes"));
    }
}
