
package com.saurabh.projectHub.dto;

import com.saurabh.projectHub.model.Task.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description = "";

    private LocalDateTime dueDate;

    private Priority priority = Priority.MEDIUM;

    @NotBlank(message = "Project ID is required")
    private String projectId;

    @NotBlank(message = "Assignee username is required")
    private String assignedToUsername;  // frontend sends username
}