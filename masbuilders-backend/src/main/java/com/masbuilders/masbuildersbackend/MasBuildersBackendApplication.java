package com.masbuilders.masbuildersbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull; // ✅ Add this import

@SpringBootApplication
public class MasBuildersBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasBuildersBackendApplication.class, args);
    }


        };

