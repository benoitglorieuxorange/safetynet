package com.globe.safetynet.config;

import com.globe.safetynet.interceptor.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**")  // Intercepter toutes les URLs
                .excludePathPatterns(    // Exclure certaines URLs si nécessaire
                        "/actuator/**",      // Endpoints actuator
                        "/swagger-ui/**",    // Swagger UI
                        "/v3/api-docs/**",   // OpenAPI docs
                        "/static/**",        // Ressources statiques
                        "/css/**",
                        "/js/**",
                        "/images/**"
                );
    }
}

