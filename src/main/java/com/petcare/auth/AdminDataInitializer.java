package com.petcare.auth;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer
        implements CommandLineRunner {

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;


    @Value("${app.admin.username:admin}")
    private String adminUsername;


    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;


    @Override
    public void run(String... args) {

        if (appUserRepository
                .existsByUsername(adminUsername)) {

            return;
        }

        AppUser admin = new AppUser();

        admin.setUsername(adminUsername);

        admin.setPassword(
                passwordEncoder.encode(
                        adminPassword
                )
        );

        admin.setRole("ADMIN");

        admin.setActive(true);

        appUserRepository.save(admin);
    }
}
