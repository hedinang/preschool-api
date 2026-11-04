package org.example.preschool.util.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.User;
import org.example.preschool.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = getBearerToken(request);
        if (token != null) {
            if (!tokenProvider.validateToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }

            Optional<User> userResult = userRepository.findByUserId(tokenProvider.getUserIdFromToken(token));
            if (userResult.isEmpty() || !token.equals(userResult.get().getAccessToken())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }

            CustomUserDetails userDetails = new CustomUserDetails(userResult.get());
            if (!userDetails.isEnabled()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Account is disabled");
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    )
            );
        }

        filterChain.doFilter(request, response);
    }

    private String getBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7).trim();
        return StringUtils.hasText(token) ? token : null;
    }
}
