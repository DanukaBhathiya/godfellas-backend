package com.dtsolution.godfellas.config;

import com.dtsolution.godfellas.entity.Role;
import com.dtsolution.godfellas.entity.User;
import com.dtsolution.godfellas.repository.UserRepository;
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
        // Create default SUPER_ADMIN if no users exist
        if (userRepository.count() == 0) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("admin123"));
            superAdmin.setEmail("superadmin@goodfellas.com");
            superAdmin.setRole(Role.SUPER_ADMIN);
            superAdmin.setActive(true);
            userRepository.save(superAdmin);
            
            System.out.println("===========================================");
            System.out.println("Default SUPER_ADMIN created:");
            System.out.println("Username: superadmin");
            System.out.println("Password: admin123");
            System.out.println("===========================================");
        }
    }
}
