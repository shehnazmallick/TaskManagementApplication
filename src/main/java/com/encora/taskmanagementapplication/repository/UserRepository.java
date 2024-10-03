package com.encora.taskmanagementapplication.repository;

import com.encora.taskmanagementapplication.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User getUsersById(Long userId);
    User findByEmail(String email);
}
