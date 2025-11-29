package com.masbuilders.masbuildersbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //  Serve ALL uploaded files (direct access)
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
}
