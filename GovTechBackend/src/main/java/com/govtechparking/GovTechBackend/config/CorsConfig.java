package com.govtechparking.GovTechBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    private final CorsProperties corsProperties;

    public CorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> origins = corsProperties.allowedOrigins();
        List<String> originPatterns = corsProperties.allowedOriginPatterns();

        if (origins != null && !origins.isEmpty()) {
            config.setAllowedOrigins(origins);
        }
        // Origin patterns allow wildcards while still supporting credentials.
        if (originPatterns != null && !originPatterns.isEmpty()) {
            config.setAllowedOriginPatterns(originPatterns);
        }
        // Sensible default if nothing configured: allow any origin (no credentials).
        if ((origins == null || origins.isEmpty())
                && (originPatterns == null || originPatterns.isEmpty())) {
            config.setAllowedOriginPatterns(List.of("*"));
        }

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Location"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
