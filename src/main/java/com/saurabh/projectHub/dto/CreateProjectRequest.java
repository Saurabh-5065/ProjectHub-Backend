package com.saurabh.projectHub.dto;

import com.saurabh.projectHub.model.Project.Priority;
import com.saurabh.projectHub.model.Project.Status;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    private Priority priority = Priority.MEDIUM;    // optional, defaults to MEDIUM

    private String description = "";                // optional

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;

    @Min(value = 0, message = "Progress cannot be less than 0")
    @Max(value = 100, message = "Progress cannot exceed 100")
    private int progress = 0;                       // optional, defaults to 0

    private Status status = Status.NOT_STARTED;     // optional

    private List<String> memberIds;                 // optional list of user IDs
}