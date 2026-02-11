package com.blog.controller;

import com.blog.model.AdminUser;
import com.blog.repository.AdminUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AdminUserRepository adminRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final Set<String> activeTokens = Collections.synchronizedSet(new HashSet<>());

    public AuthController(AdminUserRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<AdminUser> user = adminRepo.findByUsername(username);
        if (user.isPresent() && encoder.matches(password, user.get().getPasswordHash())) {
            String token = UUID.randomUUID().toString();
            activeTokens.add(token);
            return ResponseEntity.ok(Map.of("token", token));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")
                || !isValidToken(authHeader.substring(7))) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String username = body.get("username");
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        Optional<AdminUser> user = adminRepo.findByUsername(username);
        if (user.isEmpty() || !encoder.matches(currentPassword, user.get().getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Current password is incorrect"));
        }

        AdminUser admin = user.get();
        admin.setPasswordHash(encoder.encode(newPassword));
        adminRepo.save(admin);

        return ResponseEntity.ok(Map.of("message", "Password updated"));
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
