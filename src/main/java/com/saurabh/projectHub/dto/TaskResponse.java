package com.saurabh.projectHub.dto;

import com.saurabh.projectHub.model.Task.Priority;
import com.saurabh.projectHub.model.Task.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private String id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Priority priority;
    private boolean completed;
    private TaskStatus status;
    private String projectId;
    private String projectName;
    private String assignedToId;
    private String assignedToUsername;
    private String assignorId;
    private String assignorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}