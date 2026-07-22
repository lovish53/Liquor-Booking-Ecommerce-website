package com.liquor.liquor.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class OAuthFallbackController {

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @GetMapping("/oauth2/authorization/google")
    public void googleOAuthNotConfigured(HttpServletResponse response) throws IOException {
        String message = URLEncoder.encode(
                "Google OAuth is not configured. Start backend with dev,oauth profile and GOOGLE_CLIENT_ID / GOOGLE_CLIENT_SECRET.",
                StandardCharsets.UTF_8);
        response.sendRedirect(frontendUrl + "/login?error=" + message);
    }
}
