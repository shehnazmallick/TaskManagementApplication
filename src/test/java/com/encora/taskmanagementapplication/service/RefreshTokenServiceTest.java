package com.encora.taskmanagementapplication.service;

import com.encora.taskmanagementapplication.entity.RefreshToken;
import com.encora.taskmanagementapplication.exception.AuthException;
import com.encora.taskmanagementapplication.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void verifyExpiration_validToken_returnsToken() throws AuthException {
        // Arrange
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(LocalDateTime.now().plusHours(1)); // Token expires in 1 hour
        // Act
        RefreshToken result = refreshTokenService.verifyExpiration(token);
        // Assert
        assertEquals(token, result);
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_expiredToken_throwsAuthException() {
        // Arrange
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(LocalDateTime.now().minusHours(1)); // Token expired 1 hour ago
        // Act and Assert
        AuthException exception = assertThrows(AuthException.class,
                () -> refreshTokenService.verifyExpiration(token));

        assertEquals("Refresh token was expired. Please make a new signin request", exception.getMessage());
        verify(refreshTokenRepository, times(1)).delete(any(RefreshToken.class));
    }

    @Test
    void findByToken_existingToken_returnsToken() {
        // Arrange
        String tokenString = "testToken";
        RefreshToken token = new RefreshToken();
        token.setToken(tokenString);
        // Mock repository behavior
        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));
        // Act
        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);
        // Assert
        assertTrue(result.isPresent());
        assertEquals(token, result.get());
        verify(refreshTokenRepository, times(1)).findByToken(tokenString);
    }

    @Test
    void findByToken_nonExistingToken_returnsEmptyOptional() {
        // Arrange
        String tokenString = "nonExistingToken";
        // Mock repository behavior
        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.empty());
        // Act
        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);
        // Assert
        assertFalse(result.isPresent());
        verify(refreshTokenRepository, times(1)).findByToken(tokenString);
    }
}