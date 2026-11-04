package org.example.preschool.model.dto;

import org.example.preschool.model.entity.User;
import org.example.preschool.util.constant.RoleType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CustomUserDetailsTest {

    @Test
    void exposesUserRoleAsSpringAuthority() {
        User user = new User();
        user.setRole(RoleType.ADMIN);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void disablesUsersWithInactiveStatus() {
        User user = new User();
        user.setStatus("INACTIVE");

        assertFalse(new CustomUserDetails(user).isEnabled());
    }
}
