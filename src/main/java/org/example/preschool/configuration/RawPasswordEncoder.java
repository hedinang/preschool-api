package org.example.preschool.configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

public class RawPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }
}
