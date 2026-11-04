package org.example.preschool.controller;

import org.example.preschool.configuration.WebSecurityConfig;
import org.example.preschool.repository.UserRepository;
import org.example.preschool.util.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
@Import(WebSecurityConfig.class)
class HealthControllerTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsUpStatus() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"UP\"}"));
    }

    @Test
    void requiresAuthenticationForApiRoutes() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deniesCustomerAccessToAdminRoutes() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void allowsAdminAccessToAdminRoutes() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isNotFound());
    }
}
