package com.masbuilders.masbuildersbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        //  PUBLIC APIs
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/properties/**").permitAll()

                        // PUBLIC ACCESS TO UPLOADED FILES
                        .requestMatchers("/uploads/**").permitAll()

                        //  USER (Buyer / Seller / Admin – must be logged in)
                        .requestMatchers("/api/notifications/**").authenticated()

                        //  ADMIN ONLY
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        //  EVERYTHING ELSE NEEDS LOGIN
                        .anyRequest().authenticated()
                )

                //  JWT FILTER
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
