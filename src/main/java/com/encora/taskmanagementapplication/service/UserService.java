package com.encora.taskmanagementapplication.service;

import com.encora.taskmanagementapplication.dto.UserSettingsDto;
import com.encora.taskmanagementapplication.entity.User;
import com.encora.taskmanagementapplication.entity.UserSettings;
import com.encora.taskmanagementapplication.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User getUserById(Long userId) {
        return userRepository.getUsersById(userId);
    }


    public User createUser(String email, String password, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        // Hash the password before setting it
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserSettings(UserSettings.builder().showNotifications(true).build());
        return userRepository.save(user);
    }

    public Long validateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            boolean valid = passwordEncoder.matches(password, user.getPassword());
            if(valid) {
                return user.getId();
            }
        }
        return null;
    }

    public User getUserbyEmail(String userId) {
        return userRepository.findByEmail(userId);
    }

    public UserSettings updateUserSettings(String userId, UserSettingsDto userSettings) {
        User user = userRepository.findByEmail(userId);
        UserSettings dbUserSettings = user.getUserSettings();
        dbUserSettings.setShowNotifications(userSettings.isShowNotifications());
        return dbUserSettings;
    }
}
