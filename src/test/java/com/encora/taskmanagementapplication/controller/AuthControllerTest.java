package com.encora.taskmanagementapplication.controller;

import com.encora.taskmanagementapplication.dto.AuthRequest;
import com.encora.taskmanagementapplication.dto.AuthResponse;
import com.encora.taskmanagementapplication.dto.RefreshTokenRequest;
import com.encora.taskmanagementapplication.entity.RefreshToken;
import com.encora.taskmanagementapplication.entity.User;
import com.encora.taskmanagementapplication.entity.UserSettings;
import com.encora.taskmanagementapplication.exception.AuthException;
import com.encora.taskmanagementapplication.service.RefreshTokenService;
import com.encora.taskmanagementapplication.service.UserService;
import com.encora.taskmanagementapplication.util.JwtUtil;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserService userService;
    @Mock
    private Bucket bucket;
    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthController authController;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserSettings(UserSettings.builder().showNotifications(true).build());

        refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken("testRefreshToken");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
    }

    @Test
    void refreshToken_validRefreshToken_returnsNewToken() throws AuthException {
        // Arrange
        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.ofNullable(refreshToken));
        when(refreshTokenService.verifyExpiration(any(RefreshToken.class))).thenReturn(refreshToken);
        when(jwtUtil.generateToken(anyString())).thenReturn("newJwtToken");

        RefreshTokenRequest request = new RefreshTokenRequest("testRefreshToken");

        // Act
        Mono<ResponseEntity<String>> response = authController.refreshToken(request);

        // Assert
        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.OK, entity.getStatusCode());
                    assertEquals("newJwtToken", entity.getBody());
                })
                .verifyComplete();

        verify(refreshTokenService, times(1)).findByToken("testRefreshToken");
        verify(refreshTokenService, times(1)).verifyExpiration(refreshToken);
        verify(jwtUtil, times(1)).generateToken("test@example.com");
    }

    @Test
    void refreshToken_invalidRefreshToken_returnsForbidden() throws AuthException {
        // Arrange
        when(refreshTokenService.findByToken(anyString())).thenReturn(null);
        RefreshTokenRequest request = new RefreshTokenRequest("invalidRefreshToken");
        Mono<ResponseEntity<String>> response = authController.refreshToken(request);
        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
                    assertEquals("Refresh token is not in database!", entity.getBody());
                })
                .verifyComplete();
        verify(refreshTokenService, times(1)).findByToken("invalidRefreshToken");
        verify(refreshTokenService, never()).verifyExpiration(any());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void refreshToken_expiredRefreshToken_returnsForbidden() throws AuthException {
        refreshToken.setExpiryDate(LocalDateTime.now().minusMinutes(35));
        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.ofNullable(refreshToken));
        when(refreshTokenService.verifyExpiration(any(RefreshToken.class))).thenThrow(new AuthException("Refresh token was expired. Please make a new signin request"));

        RefreshTokenRequest request = new RefreshTokenRequest("testRefreshToken");
        Mono<ResponseEntity<String>> response = authController.refreshToken(request);
        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
                    // Assert that the correct error message is returned
                    assertEquals("Refresh token was expired. Please make a new signin request", entity.getBody());
                })
                .verifyComplete();
    }

    @Test
    void generateToken_validCredentials_returnsToken() {
        // Arrange
        when(bucket.tryConsume(1)).thenReturn(true);
        when(userService.validateUser(anyString(), anyString())).thenReturn(1L);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(jwtUtil.generateToken(anyString())).thenReturn("testToken");

        AuthRequest authRequest = new AuthRequest("test@example.com", "password");

        // Act
        Mono<ResponseEntity<?>> response = authController.generateToken(authRequest);

        // Assert
        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.OK, entity.getStatusCode());
                    assertTrue(entity.getBody() instanceof AuthResponse);
                    AuthResponse authResponse = (AuthResponse) entity.getBody();
                    assertEquals("testToken", authResponse.getToken());
                    assertEquals("Test User", authResponse.getUserName());
                })
                .verifyComplete();

        verify(bucket, times(1)).tryConsume(1);
        verify(userService, times(1)).validateUser("test@example.com", "password");
        verify(userService, times(1)).getUserById(1L);
        verify(jwtUtil, times(1)).generateToken("test@example.com");
    }

    @Test
    void generateToken_invalidCredentials_returnsUnauthorized() {
        // Arrange
        when(bucket.tryConsume(1)).thenReturn(true);
        when(userService.validateUser(anyString(), anyString())).thenReturn(null);

        AuthRequest authRequest = new AuthRequest("test@example.com", "wrongPassword");

        // Act
        Mono<ResponseEntity<?>> response = authController.generateToken(authRequest);

        // Assert
        StepVerifier.create(response)
                .assertNext(entity -> assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode()))
                .verifyComplete();

        verify(bucket, times(1)).tryConsume(1);
        verify(userService, times(1)).validateUser("test@example.com", "wrongPassword");
        verify(userService, never()).getUserById(anyLong());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void generateToken_rateLimitExceeded_returnsTooManyRequests() {
        // Arrange
        when(bucket.tryConsume(1)).thenReturn(false);

        AuthRequest authRequest = new AuthRequest("test@example.com", "password");

        // Act
        Mono<ResponseEntity<?>> response = authController.generateToken(authRequest);

        // Assert
        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.TOO_MANY_REQUESTS, entity.getStatusCode());
                    assertEquals("Too many requests!", entity.getBody());
                })
                .verifyComplete();

        verify(bucket, times(1)).tryConsume(1);
        verify(userService, never()).validateUser(anyString(), anyString());
        verify(userService, never()).getUserById(anyLong());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void signup_validRequest_returnsCreated() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("username", "test@example.com");
        request.put("password", "Password123!");
        request.put("firstName", "Test");
        request.put("lastName", "User");

        when(userService.createUser(anyString(), anyString(), anyString(), anyString())).thenReturn(user);

        // Act
        ResponseEntity<Map<String, String>> response = authController.signup(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully!", response.getBody().get("message"));

        verify(userService, times(1)).createUser("test@example.com", "Password123!", "Test", "User");
    }

    @Test
    void signup_invalidEmail_returnsBadRequest() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("username", "invalidemail");
        request.put("password", "Password123!");
        request.put("firstName", "Test");
        request.put("lastName", "User");

        // Act
        ResponseEntity<Map<String, String>> response = authController.signup(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().get("username"));
    }

    @Test
    void signup_invalidPassword_returnsBadRequest() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("username", "test@example.com");
        request.put("password", "weak");
        request.put("firstName", "Test");
        request.put("lastName", "User");

        // Act
        ResponseEntity<Map<String, String>> response = authController.signup(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().get("password"));
    }

    @Test
    void logout_always_returnsOkAndClearsCookie() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        ResponseEntity<String> response = authController.logout(exchange);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully", response.getBody());
        ResponseCookie cookie = exchange.getResponse().getCookies().getFirst("token");
        assertNotNull(cookie);
        assertEquals("", cookie.getValue());
        assertEquals(Duration.ZERO, cookie.getMaxAge());
    }

}