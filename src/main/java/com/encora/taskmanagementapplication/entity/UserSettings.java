package com.encora.taskmanagementapplication.entity;

import lombok.*;
import jakarta.persistence.*;

@Builder
@Setter
@Getter
@Entity
@Table(name = "user_settings")
@AllArgsConstructor
@NoArgsConstructor
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "show_notifications", nullable = false, columnDefinition = "boolean default true")
    private boolean showNotifications;

}
