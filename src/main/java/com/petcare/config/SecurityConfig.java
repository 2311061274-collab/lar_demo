package com.petcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/chat/**"))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/about",
                                "/contact",
                                "/about",
                                "/services/**",
                                "/news/**",
                                "/pricing",
                                "/news/**",
                                "/contact",
                                "/appointments/**",
                                "/branches",
                                "/reviews/**",
                                "/chat/**",
                                "/api/chat/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/admin/login"
                        )
                        .permitAll()

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .anyRequest()
                        .permitAll()
                )

                .formLogin(form -> form

                        .loginPage("/admin/login")

                        .loginProcessingUrl("/admin/login")

                        .defaultSuccessUrl(
                                "/admin/dashboard",
                                true
                        )

                        .failureUrl(
                                "/admin/login?error"
                        )

                        .permitAll()
                )

                .logout(logout -> logout

                        .logoutUrl("/admin/logout")

                        .logoutSuccessUrl(
                                "/admin/login?logout"
                        )

                        .permitAll()
                );

        return http.build();
    }
}
