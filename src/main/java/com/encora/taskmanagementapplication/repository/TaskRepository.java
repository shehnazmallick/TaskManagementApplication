package com.encora.taskmanagementapplication.repository;

import com.encora.taskmanagementapplication.entity.Task;
import com.encora.taskmanagementapplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    Task getTasksById(Long taskId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo = :user")
    int getTasksCountByAssignedTo(@Param("user") User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo = :user AND t.status = :status")
    int getTasksCountByAssignedToAndStatus(@Param("user") User user, @Param("status") String status);

    @Modifying
    @Query("UPDATE Task t SET t.title = :#{#task.title}, t.description = :#{#task.description}, " +
            "t.status = :#{#task.status}, t.dueDate = :#{#task.dueDate} " +
            "WHERE t.id = :#{#task.id} AND t.assignedTo = :user")
    int updateTaskByAssignedTo(@Param("task") Task task, @Param("user") User user);

    Page<Task> findByAssignedTo(User user, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.dueDate = CURRENT_DATE AND t.status != 'Completed' " +
            "and t.assignedTo= :user")
    List<Task> findDueTasksTodayByUser(@Param("user") User user);

    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.status != 'Completed'" +
            "and t.assignedTo= :user")
    List<Task> findOverdueTasksByUser(@Param("user") User user);

    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user AND t.id = :taskId")
    Task findTaskByAssignedTo(@Param("user") User user, @Param("taskId") Long taskId);
}
