package com.encora.taskmanagementapplication.controller;

import com.encora.taskmanagementapplication.dto.UserSettingsDto;
import com.encora.taskmanagementapplication.entity.UserSettings;
import com.encora.taskmanagementapplication.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/settings")
    public void saveUserSettings(@RequestParam String userId, @RequestBody UserSettingsDto userSettings) {
        userService.updateUserSettings(userId, userSettings);
    }
}
