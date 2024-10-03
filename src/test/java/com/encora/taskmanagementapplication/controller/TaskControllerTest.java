package com.encora.taskmanagementapplication.controller;

import com.encora.taskmanagementapplication.dto.TaskCountInfo;
import com.encora.taskmanagementapplication.entity.Task;
import com.encora.taskmanagementapplication.service.TaskService;
import com.encora.taskmanagementapplication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;
    @Mock
    private UserService userService;

    @InjectMocks
    private TaskController taskController;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setDueDate(LocalDate.now());
        task.setStatus("Pending");
    }

    @Test
    void getAllTasks_returnsAllTasks() {
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task));
        when(taskService.getAllTasksForUser(anyString(), anyInt(), anyInt())).thenReturn(taskPage);
        List<Task> tasks = taskController.getTasksForUser("testUser", 0, 10);
        StepVerifier.create(Flux.fromIterable(tasks))
                .expectNext(task)
                .verifyComplete();
        verify(taskService, times(1)).getAllTasksForUser("testUser", 0, 10);
    }

    @Test
    void getTaskById_validId_returnsTask() {
        when(taskService.getTask("testUser", 1L)).thenReturn(task);
        Task response = taskController.getTask("testUser",1L);
        StepVerifier.create(Mono.just(response))
                .assertNext(entity -> {
                    assertEquals(task, entity);
                })
                .verifyComplete();

        verify(taskService, times(1)).getTask("testUser", 1L);
    }

    @Test
    void createTask_validTask_returnsCreatedTask() throws Exception {
        when(taskService.createUpdateTask(any(Task.class), anyString()))
                .thenReturn(task);
        Task response = taskController.updateTask(task, "testUser");
        StepVerifier.create(Mono.just(response))
                .assertNext(entity -> {
                    assertEquals(task, entity);
                })
                .verifyComplete();
        verify(taskService, times(1)).createUpdateTask(task, "testUser");
    }

    @Test
    void updateTask_validTask_returnsUpdatedTask() throws Exception {
        when(taskService.createUpdateTask(any(Task.class), anyString()))
                .thenReturn(task);
        Task response = taskController.updateTask(task, "testUser");
        StepVerifier.create(Mono.just(response))
                .assertNext(entity -> {
                    assertEquals(task, entity);
                })
                .verifyComplete();
        verify(taskService, times(1)).createUpdateTask(task, "testUser");
    }


    @Test
    void getTotalTasks_shouldReturnTaskCountInfo() {
        // Mock data
        String userId = "testUser";
        TaskCountInfo expectedTaskCountInfo = new TaskCountInfo(10, 5, 3, 2); // Example values

        // Mock service method call
        when(taskService.getTasksCount(userId)).thenReturn(expectedTaskCountInfo);

        // Call the controller method
        TaskCountInfo actualTaskCountInfo = taskController.getTotalTasks(userId);

        // Verify the results
        assertEquals(expectedTaskCountInfo, actualTaskCountInfo);
        verify(taskService, times(1)).getTasksCount(userId);
    }
}