package com.blog.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${blog.admin.username:admin}")
    private String adminUsername;

    @Value("${blog.admin.password:admin}")
    private String adminPassword;

    private static final Set<String> activeTokens = Collections.synchronizedSet(new HashSet<>());

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            String token = UUID.randomUUID().toString();
            activeTokens.add(token);
            return ResponseEntity.ok(Map.of("token", token));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            activeTokens.remove(authHeader.substring(7));
        }
        return ResponseEntity.noContent().build();
    }

    public static boolean isValidToken(String token) {
        return activeTokens.contains(token);
    }
}
