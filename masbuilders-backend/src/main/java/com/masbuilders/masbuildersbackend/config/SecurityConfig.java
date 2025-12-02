package com.masbuilders.masbuildersbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                // 🔒 Disable CSRF for JWT usage
                .csrf(csrf -> csrf.disable())

                // 🌍 Enable CORS globally
                .cors(cors -> {})

                // 🚫 Stateless session management (no cookies)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // ✅ Allow browser CORS preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ Public endpoints
                        .requestMatchers(
                                "/api/auth/**",             // login/register
                                "/uploads/**",              // static files
                                "/api/properties/approved", // buyer browsing
                                "/api/properties/search"    // buyer filters
                        ).permitAll()

                        // ✅ Buyer favorites (allow add/remove/view)
                        .requestMatchers(
                                "/api/buyer/favorites/**",
                                "/api/buyer/favorites/*"
                        ).permitAll()

                        // ✅ Buyer interests (optional, also allow)
                        .requestMatchers("/api/buyer/interest/**").permitAll()

                        // ✅ Admin routes
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ✅ Seller routes
                        .requestMatchers(
                                "/api/properties/add/**",
                                "/api/properties/update/**",
                                "/api/properties/delete/**",
                                "/api/properties/seller/**"
                        ).hasRole("SELLER")

                        // ✅ Notifications
                        // Sellers → can access their own via /my
                        .requestMatchers("/api/notifications/my/**").authenticated()
                        // Admin → can access all notifications
                        .requestMatchers("/api/notifications/**").hasAnyRole("ADMIN")

                        // ✅ Everything else
                        .anyRequest().authenticated()
                )

                // 🔑 Add JWT validation filter before Spring's auth
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
