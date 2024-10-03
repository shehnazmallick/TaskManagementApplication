package com.encora.taskmanagementapplication.service;

import com.encora.taskmanagementapplication.entity.RefreshToken;
import com.encora.taskmanagementapplication.exception.AuthException;
import com.encora.taskmanagementapplication.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

//    public RefreshToken createRefreshToken(User user) {
//        long refreshExpirationMs = 3600000L;
//        RefreshToken refreshToken = new RefreshToken(user, UUID.randomUUID().toString(),
//                LocalDateTime.now().plusMinutes(refreshExpirationMs / (1000 * 60)));
//        return refreshTokenRepository.save(refreshToken);
//    }

    public RefreshToken verifyExpiration(RefreshToken token) throws AuthException {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new AuthException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}