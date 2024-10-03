package com.encora.taskmanagementapplication.service;

import com.encora.taskmanagementapplication.entity.Task;
import com.encora.taskmanagementapplication.dto.TaskCountInfo;
import com.encora.taskmanagementapplication.dto.TasksDueStatusList;
import com.encora.taskmanagementapplication.entity.User;
import com.encora.taskmanagementapplication.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public Page<Task> getAllTasksForUser(String userId, int page, int size) {
        User user = userService.getUserbyEmail(userId);
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findByAssignedTo(user, pageable);
    }

    @Transactional
    public Task createUpdateTask(Task task, String userId) throws Exception {
        User user = userService.getUserbyEmail(userId);
        if(user == null) {
            throw new Exception("User not found");
        } else {
            if(task.getId() == null) {
                //Indicates that a new task is to be created.
                task.setAssignedTo(user);
                return taskRepository.save(task);
            } else {
                int updated = taskRepository.updateTaskByAssignedTo(task, user);
                if(updated == 0) {
                    throw new Exception("Task not updated");
                } else {
                    return taskRepository.getTasksById(task.getId());
                }
            }

        }
    }

    public void deleteTask(Long taskId) throws Exception {
        Task task = taskRepository.getTasksById(taskId);
        if(task == null) {
            throw new Exception("Task not found");
        } else {
            taskRepository.delete(task);
        }
    }

    public TaskCountInfo getTasksCount(String userId) {
        User user = userService.getUserbyEmail(userId);
        int totalTasks = taskRepository.getTasksCountByAssignedTo(user);
        int totalPendingTasks = taskRepository.getTasksCountByAssignedToAndStatus(user, "Pending");
        int totalCompletedTasks = taskRepository.getTasksCountByAssignedToAndStatus(user, "Completed");
        int totalInProgressTasks = taskRepository.getTasksCountByAssignedToAndStatus(user, "In Progress");

        return TaskCountInfo.builder().
                totalTasks(totalTasks).
                totalPendingTasks(totalPendingTasks).
                totalCompletedTasks(totalCompletedTasks).
                totalInProgressTasks(totalInProgressTasks).
                build();
    }

    public TasksDueStatusList getTaskDueStatus(String userId) {
        User user = userService.getUserbyEmail(userId);
        List<Task> dueToday = taskRepository.findDueTasksTodayByUser(user);
        List<Task> overDue = taskRepository.findOverdueTasksByUser(user);
        return new TasksDueStatusList(dueToday, overDue);
    }

    public Task getTask(String userId, Long taskId) {
        User user = userService.getUserbyEmail(userId);
        return taskRepository.findTaskByAssignedTo(user, taskId);
    }
}
