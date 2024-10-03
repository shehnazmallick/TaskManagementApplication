package com.encora.taskmanagementapplication.controller;


import com.encora.taskmanagementapplication.entity.Task;
import com.encora.taskmanagementapplication.dto.TaskCountInfo;
import com.encora.taskmanagementapplication.dto.TasksDueStatusList;
import com.encora.taskmanagementapplication.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getTasksForUser(@RequestParam String userId, @RequestParam int page, @RequestParam int size) {
        return taskService.getAllTasksForUser(userId, page, size).getContent();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task, String userId) throws Exception {
        return taskService.createUpdateTask(task, userId);
    }

    @PatchMapping
    public Task updateTask(@RequestBody Task task, String userId) throws Exception {
        return taskService.createUpdateTask(task, userId);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId) throws Exception {
        taskService.deleteTask(taskId);
    }

    @GetMapping("/count")
    public TaskCountInfo getTotalTasks(String userId) {
        return taskService.getTasksCount(userId);
    }

    @GetMapping("/dueStatus")
    public TasksDueStatusList getTaskDueStatus(String userId) {
        return taskService.getTaskDueStatus(userId);
    }

    @GetMapping("/task/{taskId}")
    public Task getTask(@RequestParam String userId, @PathVariable Long taskId) {
        return taskService.getTask(userId, taskId);
    }

}
