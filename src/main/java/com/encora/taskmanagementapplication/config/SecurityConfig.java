package com.encora.taskmanagementapplication.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for JWT (state is not stored on the server)
                .authorizeExchange(exhanges -> exhanges
                        .pathMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/signup", "api/auth/refreshtoken").permitAll()
                                // ... other endpoint configurations using pathMatchers() ...//
                        .anyExchange().authenticated()) // Authenticate all other requests
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)// Disable basic authentication
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)// Disable form-based login
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource())) // Add CORS configuration
                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Update with your React app's origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("*")); // Be more specific in production
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

//    @Bean
//    public AuthenticationWebFilter authenticationWebFilter() {
//        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(jwtAuthenticationFilter);
//        authenticationWebFilter.setSecurityContextRepository(jwtAuthenticationFilter);
//        return authenticationWebFilter;
//    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
