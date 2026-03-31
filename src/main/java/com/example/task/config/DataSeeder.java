package com.example.task.config;

import com.example.task.entity.Role;
import com.example.task.entity.User;
import com.example.task.enums.RoleType;
import com.example.task.repository.RoleRepository;
import com.example.task.repository.UserRepository;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256); // آمن لـ HS256
        String base64Key = java.util.Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Generated JWT Key (Base64): " + base64Key);

        if (roleRepository.count() == 0) {
            Role adminRole = roleRepository.save(Role.builder().roleType(RoleType.ADMIN).build());
            Role userRole  = roleRepository.save(Role.builder().roleType(RoleType.USER).build());

            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .build());

            userRepository.save(User.builder()
                    .username("mahmoud")
                    .password(passwordEncoder.encode("user123"))
                    .role(userRole)
                    .build());
        }
    }
}