package com.shopmanager.controller.auth;

import com.shopmanager.entity.User;
import com.shopmanager.repository.UserRepository;
import com.shopmanager.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username and password are required"));
            }

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Invalid username or password"));

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid username or password");
            }

            String token = jwtUtil.generateToken(user);

            return ResponseEntity.ok(
                    Map.of(
                            "token", token,
                            "username", user.getUsername(),
                            "role", user.getRole().name()
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }
}