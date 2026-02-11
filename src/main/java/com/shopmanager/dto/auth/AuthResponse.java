package com.shopmanager.dto.auth;

import com.shopmanager.entity.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String username;
    private Role role;
    private String token;
}