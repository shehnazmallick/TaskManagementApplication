package com.encora.taskmanagementapplication.repository;

import com.encora.taskmanagementapplication.entity.UserSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingsRepository extends CrudRepository<UserSettings, Long> {
}
