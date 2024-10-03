package com.encora.taskmanagementapplication.dto;

import com.encora.taskmanagementapplication.entity.UserSettings;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuthResponse {
    private String token;
    private String userName;
    private UserSettings userSettings;
}
