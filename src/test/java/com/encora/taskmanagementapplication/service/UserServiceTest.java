package com.encora.taskmanagementapplication.service;

import com.encora.taskmanagementapplication.dto.UserSettingsDto;
import com.encora.taskmanagementapplication.entity.User;
import com.encora.taskmanagementapplication.entity.UserSettings;
import com.encora.taskmanagementapplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserSettings userSettings;

    @BeforeEach
    void setUp() {
        userSettings = UserSettings.builder()
                .id(1L)
                .showNotifications(true)
                .build();

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .userSettings(userSettings)
                .build();
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.getUsersById(anyLong())).thenReturn(user);
        User result = userService.getUserById(1L);
        assertEquals(user, result);
    }

    @Test
    void createUser_shouldReturnUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        User result = userService.createUser("test@example.com", "password", "Test", "User");
        assertEquals(user, result);
    }

    @Test
    void validateUser_validCredentials_shouldReturnUserId() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        Long userId = userService.validateUser("test@example.com", "password");
        assertEquals(user.getId(), userId);
    }

    @Test
    void validateUser_invalidCredentials_shouldReturnNull() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        Long userId = userService.validateUser("test@example.com", "wrongPassword");
        assertNull(userId);
    }

    @Test
    void getUserbyEmail_shouldReturnUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        User result = userService.getUserbyEmail("test@example.com");
        assertEquals(user, result);
    }

    @Test
    void updateUserSettings_shouldUpdateUserSettings() {
        UserSettingsDto userSettingsDto = new UserSettingsDto();
        userSettingsDto.setShowNotifications(false);

        when(userRepository.findByEmail(anyString())).thenReturn(user);
        UserSettings updatedSettings = userService.updateUserSettings("test@example.com", userSettingsDto);

        assertEquals(false, updatedSettings.isShowNotifications());
    }
}