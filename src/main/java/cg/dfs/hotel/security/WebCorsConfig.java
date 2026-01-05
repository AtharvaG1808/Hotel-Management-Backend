
// src/main/java/cg/dfs/hotel/security/WebCorsConfig.java
package cg.dfs.hotel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class WebCorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();

        // ✅ match your frontend origin exactly
        cors.setAllowedOrigins(List.of("http://localhost:3000"));

        // Alternatively for dev flexibility:
        // cors.setAllowedOriginPatterns(List.of("http://localhost:*"));

        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cors.setExposedHeaders(List.of("Authorization"));
        cors.setAllowCredentials(true);   // OK with Authorization header; important if you later use cookies
        cors.setMaxAge(3600L);            // cache preflight for 1h

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // ✅ apply to your API path
        source.registerCorsConfiguration("/api/**", cors);
        // If you have auth endpoints outside /api, register them too.
        return source;
    }
}
