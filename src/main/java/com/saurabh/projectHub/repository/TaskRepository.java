package com.saurabh.projectHub.repository;

import com.saurabh.projectHub.model.Task;
import com.saurabh.projectHub.model.Task.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {

    // All tasks assigned to a user (getMyTasks)
    List<Task> findByAssignedToIdOrderByCreatedAtDesc(String assignedToId);

    // All tasks created by a user/assignor (getTaskInReview)
    List<Task> findByAssignorIdOrderByCreatedAtDesc(String assignorId);

    // All tasks for a specific project
    List<Task> findByProjectIdOrderByCreatedAtDesc(String projectId);

    // Tasks by project and status
    List<Task> findByProjectIdAndStatus(String projectId, TaskStatus status);
}