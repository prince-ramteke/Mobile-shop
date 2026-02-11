package com.shopmanager.service;

import com.shopmanager.dto.auth.AuthRequest;
import com.shopmanager.dto.auth.AuthResponse;
import com.shopmanager.entity.User;

public interface UserService {

    User loadUserByUsername(String username);

    User createAdminUser();

    AuthResponse login(AuthRequest request);
}