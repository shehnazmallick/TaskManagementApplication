package com.encora.taskmanagementapplication.controller;

import com.encora.taskmanagementapplication.dto.AuthRequest;
import com.encora.taskmanagementapplication.dto.AuthResponse;
import com.encora.taskmanagementapplication.dto.RefreshTokenRequest;
import com.encora.taskmanagementapplication.entity.User;
import com.encora.taskmanagementapplication.exception.AuthException;
import com.encora.taskmanagementapplication.service.RefreshTokenService;
import com.encora.taskmanagementapplication.service.UserService;
import com.encora.taskmanagementapplication.util.JwtUtil;
import io.github.bucket4j.Bucket;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.encora.taskmanagementapplication.entity.RefreshToken;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final Bucket bucket;
    private RefreshTokenService refreshTokenService;

    public AuthController(JwtUtil jwtUtil, UserService userService, Bucket bucket, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.bucket = bucket;
        this.refreshTokenService = refreshTokenService;

    }

    @PostMapping("/refreshtoken")
    public Mono<ResponseEntity<String>> refreshToken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return Mono.justOrEmpty(refreshTokenService.findByToken(requestRefreshToken))
                .flatMap(refreshToken -> { // Use flatMap to handle the error within the stream
                    try {
                        RefreshToken verifiedToken = refreshTokenService.verifyExpiration(refreshToken);
                        String token = jwtUtil.generateToken(verifiedToken.getUser().getEmail());
                        return Mono.just(ResponseEntity.ok(token));
                    } catch (AuthException e) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()));
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Refresh token is not in database!")));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> generateToken(@RequestBody AuthRequest authRequest) {
        boolean probe = bucket.tryConsume(1);
        if (probe) {
            Long userId = userService.validateUser(authRequest.getUsername(), authRequest.getPassword());
            if (userId != null) {
                String token = jwtUtil.generateToken(authRequest.getUsername());
                User user = userService.getUserById(userId);
                AuthResponse response = AuthResponse.builder()
                        .token(token)
                        .userName(user.getFirstName() + " " + user.getLastName())
                        .userSettings(user.getUserSettings())
                        .build();
                return Mono.just(ResponseEntity.ok(response));
            } else {
                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
            }
        } else {
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests!"));
        }
    }

     @PostMapping("/signup")
     public ResponseEntity<Map<String, String>> signup(@RequestBody Map<String, String> request) {
         String username = request.get("username");
         String password = request.get("password");
         String firstName = request.get("firstName");
         String lastName = request.get("lastName");

         Map<String, String> errors = validate(username, password);
         if (!errors.isEmpty()) {
             return ResponseEntity.badRequest().body(errors);
         }
         userService.createUser(username, password, firstName, lastName);
         return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully!"));
     }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(ServerWebExchange exchange) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true) // Set to true if using HTTPS
                .build();
        // Add the cookie to the response
        exchange.getResponse().addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }

    private Map<String, String> validate(String username, String password) {
        Map<String, String> errors = new HashMap<>();

        // Email validation
        if (username == null || !username.matches(".+@.+\\..+")) {
            errors.put("username", "Please enter a valid email address.");
        }

        // Password validation
        if (password == null || password.length() < 8
                || !password.matches(".*[A-Z].*")
                || !password.matches(".*[0-9].*")
                || !password.matches(".*[^A-Za-z0-9].*")) {
            errors.put("password", "Password must be at least 8 characters long and contain at least one uppercase letter, one number, and one special character.");
        }

        return errors;
    }
}