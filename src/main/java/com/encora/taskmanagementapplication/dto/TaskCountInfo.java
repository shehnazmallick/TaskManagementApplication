package com.encora.taskmanagementapplication.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class TaskCountInfo {
    private int totalTasks;
    private int totalPendingTasks;
    private int totalCompletedTasks;
    private int totalInProgressTasks;
}
