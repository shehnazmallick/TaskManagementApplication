package com.encora.taskmanagementapplication.controller;

import com.encora.taskmanagementapplication.dto.UserSettingsDto;
import com.encora.taskmanagementapplication.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void saveUserSettings_shouldCallServiceToUpdateSettings() {
        // Mock data
        String userId = "testUser";
        UserSettingsDto userSettingsDto = new UserSettingsDto();

        // Call the controller method
        userController.saveUserSettings(userId, userSettingsDto);

        // Verify that the service method was called with the correct arguments
        verify(userService, times(1)).updateUserSettings(userId, userSettingsDto);
    }
}