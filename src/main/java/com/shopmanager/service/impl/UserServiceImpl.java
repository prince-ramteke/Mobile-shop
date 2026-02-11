package com.shopmanager.service.impl;

import com.shopmanager.dto.auth.AuthRequest;
import com.shopmanager.dto.auth.AuthResponse;
import com.shopmanager.entity.User;
import com.shopmanager.entity.enums.Role;
import com.shopmanager.repository.UserRepository;
import com.shopmanager.security.JwtUtil;
import com.shopmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User createAdminUser() {
        if (userRepository.existsByUsername("admin")) {
            return userRepository.findByUsername("admin").orElseThrow();
        }

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        return userRepository.save(admin);
    }

    @Override
    public AuthResponse login(AuthRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}