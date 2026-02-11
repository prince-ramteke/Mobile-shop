package com.shopmanager.security;

import com.shopmanager.entity.User;
import com.shopmanager.entity.enums.Role;
import com.shopmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminUser();
        createStaffUser();
    }

    private void createAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);

            userRepository.save(admin);
            System.out.println("✅ ADMIN User Created: username=admin, password=admin123");
        } else {
            System.out.println("ℹ️ ADMIN User key already exists (Skipping creation)");
        }
    }

    private void createStaffUser() {
        if (userRepository.findByUsername("staff").isEmpty()) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setPassword(passwordEncoder.encode("staff123"));
            staff.setRole(Role.STAFF);
            staff.setEnabled(true);

            userRepository.save(staff);
            System.out.println("✅ STAFF User Created: username=staff, password=staff123");
        } else {
            System.out.println("ℹ️ STAFF User key already exists (Skipping creation)");
        }
    }
}