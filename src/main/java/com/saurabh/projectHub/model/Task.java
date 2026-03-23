package com.saurabh.projectHub.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Data
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    private String title;

    private String description = "";

    private LocalDateTime dueDate;

    private Priority priority = Priority.MEDIUM;

    private boolean completed = false;

    private TaskStatus status = TaskStatus.IN_PROGRESS;

    @DBRef
    private Project project;

    @DBRef
    private User assignedTo;        // member receiving the task

    @DBRef
    private User assignor;          // team lead who created the task

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum TaskStatus {
        IN_PROGRESS, IN_REVIEW, COMPLETED
    }
}