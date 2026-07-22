package com.liquor.liquor.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.liquor.liquor.entity.Role;
import com.liquor.liquor.entity.UserEntity;
import com.liquor.liquor.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        if (email == null || email.isBlank()) {
            response.sendRedirect(frontendUrl + "/login?error=oauth_email_missing");
            return;
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(UserEntity.builder()
                        .name(name == null || name.isBlank() ? email : name)
                        .email(email)
                        .phoneNumber(generatePhoneNumber(email))
                        .password(passwordEncoder.encode("oauth2:" + email))
                        .role(Role.USER)
                        .active(true)
                        .build()));

        String token = jwtService.generateToken(user);
        String redirect = frontendUrl + "/auth/callback"
                + "?token=" + encode(token)
                + "&id=" + encode(user.getId().toString())
                + "&name=" + encode(user.getName())
                + "&email=" + encode(user.getEmail())
                + "&phoneNumber=" + encode(user.getPhoneNumber())
                + "&role=" + encode(user.getRole().name());

        response.sendRedirect(redirect);
    }

    private String generatePhoneNumber(String email) {
        int hash = Math.abs(email.hashCode());
        return String.format("8%09d", hash % 1_000_000_000);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
