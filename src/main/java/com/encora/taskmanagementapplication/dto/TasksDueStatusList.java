package com.encora.taskmanagementapplication.dto;

import com.encora.taskmanagementapplication.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TasksDueStatusList {
    List<Task> dueToday;
    List<Task> overDue;
}
