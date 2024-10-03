package com.encora.taskmanagementapplication.config;

import com.encora.taskmanagementapplication.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && jwtUtil.validateToken(token, username)) {
                // Update Uri if userId is not already present
                if (!request.getURI().toString().contains("userId")) {
                    String newUri = UriComponentsBuilder.fromUri(request.getURI())
                            .queryParam("userId", username)
                            .toUriString();
                    // Create a new ServerHttpRequest with the userId parameter
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .uri(request.getURI().resolve(newUri)) // Resolve against the base URI
                            .build();

                    // Update the ServerWebExchange with the mutated request
                    exchange = exchange.mutate().request(mutatedRequest).build();
                }
                Authentication auth = new UsernamePasswordAuthenticationToken(username, null, null);
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            }
        }
        return chain.filter(exchange);
    }
}