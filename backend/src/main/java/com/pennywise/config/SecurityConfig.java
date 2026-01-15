package com.pennywise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 * 
 * <p>
 * Currently configured for development with minimal security.
 * TODO: Add JWT authentication before production.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for API (we'll use JWT tokens)
                .csrf(csrf -> csrf.disable())

                // Stateless session - no server-side sessions
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure endpoint security
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // All other endpoints require authentication (disabled for now)
                        // TODO: Enable this when JWT is implemented
                        // .anyRequest().authenticated()
                        .anyRequest().permitAll());

        return http.build();
    }
}
