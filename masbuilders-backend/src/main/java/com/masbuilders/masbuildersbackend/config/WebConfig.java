package com.masbuilders.masbuildersbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * ✅ 1. Serve static uploaded resources (images/videos)
     * Preserves your current working configuration.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Serve all uploaded files (direct access)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600);

        // Serve uploaded images (if you use sub-folder later)
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:uploads/images/")
                .setCachePeriod(3600);

        // Serve uploaded videos (if you use sub-folder later)
        registry.addResourceHandler("/uploads/videos/**")
                .addResourceLocations("file:uploads/videos/")
                .setCachePeriod(3600);
    }

    /**
     * ✅ 2. Global CORS configuration for local dev (Next.js frontend)
     * Allows secure communication between frontend (localhost:3000)
     * and backend (localhost:8081) while preserving credentials.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000") // ✅ exact origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true); // ✅ allow cookies/tokens
            }
        };
    }
}
