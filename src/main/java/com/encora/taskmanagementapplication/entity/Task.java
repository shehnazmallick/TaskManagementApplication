package com.encora.taskmanagementapplication.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "task")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @ManyToOne
    @JoinColumn(name = "user_profile_id")
    private User assignedTo;

    @PrePersist
    public void insert() {
        createDate = LocalDate.now();
        status = "Pending";
    }
}
