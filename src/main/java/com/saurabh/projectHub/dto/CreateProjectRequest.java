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

    private Priority priority = Priority.MEDIUM;

    private String description = "";

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;

    @Min(0) @Max(100)
    private int progress = 0;

    private Status status = Status.NOT_STARTED;

    // Usernames to invite — invitations sent after project creation
    private List<String> inviteUsernames;
}