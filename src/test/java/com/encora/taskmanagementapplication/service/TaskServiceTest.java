package com.encora.taskmanagementapplication.service;

import com.encora.taskmanagementapplication.dto.TaskCountInfo;
import com.encora.taskmanagementapplication.dto.TasksDueStatusList;
import com.encora.taskmanagementapplication.entity.Task;
import com.encora.taskmanagementapplication.entity.User;
import com.encora.taskmanagementapplication.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .assignedTo(user)
                .build();
    }

    @Test
    void getAllTasksForUser_shouldReturnPageOfTasks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> expectedPage = new PageImpl<>(List.of(task), pageable, 1);
        when(userService.getUserbyEmail(anyString())).thenReturn(user);
        when(taskRepository.findByAssignedTo(any(User.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<Task> actualPage = taskService.getAllTasksForUser("test@example.com", 0, 10);

        assertEquals(expectedPage, actualPage);
    }

//    @Test
//    void createUpdateTask_createNewTask_shouldReturnSavedTask() throws Exception {
//        when(userService.getUserbyEmail(anyString())).thenReturn(user);
//        when(taskRepository.updateTaskByAssignedTo(any(Task.class), any(User.class))).thenReturn(1);
//
//        Task createdTask = taskService.createUpdateTask(task, "test@example.com");
//
//        assertEquals(task, createdTask);
//    }

    @Test
    void createUpdateTask_updateExistingTask_shouldReturnUpdatedTask() throws Exception {
        task.setId(1L);
        when(userService.getUserbyEmail(anyString())).thenReturn(user);
        when(taskRepository.updateTaskByAssignedTo(any(Task.class), any(User.class))).thenReturn(1);
        when(taskRepository.getTasksById(anyLong())).thenReturn(task);

        Task updatedTask = taskService.createUpdateTask(task, "test@example.com");

        assertEquals(task, updatedTask);
    }

    @Test
    void createUpdateTask_userNotFound_shouldThrowException() {
        when(userService.getUserbyEmail(anyString())).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () ->
                taskService.createUpdateTask(task, "test@example.com"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createUpdateTask_taskNotUpdated_shouldThrowException() {
        task.setId(1L);
        when(userService.getUserbyEmail(anyString())).thenReturn(user);
        when(taskRepository.updateTaskByAssignedTo(any(Task.class), any(User.class))).thenReturn(0);

        Exception exception = assertThrows(Exception.class, () ->
                taskService.createUpdateTask(task, "test@example.com"));

        assertEquals("Task not updated", exception.getMessage());
    }

    @Test
    void deleteTask_existingTask_shouldDeleteTask() throws Exception {
        when(taskRepository.getTasksById(anyLong())).thenReturn(task);

        taskService.deleteTask(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_nonExistingTask_shouldThrowException() {
        when(taskRepository.getTasksById(anyLong())).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> taskService.deleteTask(1L));

        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    void getTasksCount_shouldReturnTaskCountInfo() {
        when(userService.getUserbyEmail(anyString())).thenReturn(user);
        when(taskRepository.getTasksCountByAssignedTo(any(User.class))).thenReturn(10);
        when(taskRepository.getTasksCountByAssignedToAndStatus(any(User.class), eq("Pending"))).thenReturn(5);
        when(taskRepository.getTasksCountByAssignedToAndStatus(any(User.class), eq("Completed"))).thenReturn(3);
        when(taskRepository.getTasksCountByAssignedToAndStatus(any(User.class), eq("In Progress"))).thenReturn(2);

        TaskCountInfo taskCountInfo = taskService.getTasksCount("test@example.com");

        assertEquals(10, taskCountInfo.getTotalTasks());
        assertEquals(5, taskCountInfo.getTotalPendingTasks());
        assertEquals(3, taskCountInfo.getTotalCompletedTasks());
        assertEquals(2, taskCountInfo.getTotalInProgressTasks());
    }

    @Test
    void getTaskDueStatus_shouldReturnTasksDueStatusList() {
        when(userService.getUserbyEmail(anyString())).thenReturn(user);
        when(taskRepository.findDueTasksTodayByUser(any(User.class))).thenReturn(Collections.singletonList(task));
        when(taskRepository.findOverdueTasksByUser(any(User.class))).thenReturn(Collections.emptyList());

        TasksDueStatusList tasksDueStatusList = taskService.getTaskDueStatus("test@example.com");

        assertEquals(1, tasksDueStatusList.getDueToday().size());
        assertEquals(0, tasksDueStatusList.getOverDue().size());
    }

    @Test
    void getTask_shouldReturnTask() {
        when(userService.getUserbyEmail(anyString())).thenReturn(user);
        when(taskRepository.findTaskByAssignedTo(any(User.class), anyLong())).thenReturn(task);

        Task returnedTask = taskService.getTask("test@example.com", 1L);

        assertEquals(task, returnedTask);
    }
}