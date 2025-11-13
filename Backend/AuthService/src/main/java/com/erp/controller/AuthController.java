package com.erp.controller;

import com.erp.entity.AuthRequest;
import com.erp.entity.Role;
import com.erp.entity.User;
import com.erp.security.JwtUtil;
import com.erp.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin("http://localhost:4200")
public class AuthController {

    private final CustomUserDetailsService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(CustomUserDetailsService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ LOGIN ENDPOINT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            User user = userService.findByUsernameIgnoreCase(request.getUsername())
                    .orElseThrow(() -> new Exception("Invalid username or password"));

            // 🔐 Check password
            if (!userService.passwordMatches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Invalid username or password"));
            }

            // 🔐 Check selected role matches user’s roles
            boolean roleMatch = user.getRoles().stream()
                    .anyMatch(r -> r.getRoleName().equalsIgnoreCase(request.getRole()));

            if (!roleMatch) {
                return ResponseEntity.status(403)
                        .body(Map.of("message", "Access denied: Role mismatch"));
            }

            // 🔐 Generate JWT
            String token = jwtUtil.generateToken(user.getUsername(), request.getRole());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", request.getRole(),
                    "username", user.getUsername()
            ));

        } catch (Exception ex) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", ex.getMessage()));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            String roleName = body.get("role");

            if (username == null || password == null || roleName == null) {
                return ResponseEntity.status(400)
                        .body(Map.of("message", "Missing required fields"));
            }

            if (userService.findByUsernameIgnoreCase(username).isPresent()) {
                return ResponseEntity.status(400)
                        .body(Map.of("message", "User already exists"));
            }

            // ✅ Encode password
            String encodedPassword = passwordEncoder.encode(password);

            // ✅ Create user and assign role
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(encodedPassword);
            newUser.setEnabled(1);

            // ✅ Fetch or create role
            Role role = userService.getOrCreateRole(roleName.toUpperCase());
            newUser.getRoles().add(role);

            // ✅ Save user
            userService.saveUser(newUser);

            return ResponseEntity.status(201).body(Map.of(
                    "message", "User registered successfully",
                    "username", username,
                    "role", roleName
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

}
