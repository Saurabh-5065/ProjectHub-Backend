package com.saurabh.projectHub.controller;

import com.saurabh.projectHub.dto.CreateTaskRequest;
import com.saurabh.projectHub.dto.TaskResponse;
import com.saurabh.projectHub.dto.UpdateTaskStatusRequest;
import com.saurabh.projectHub.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody CreateTaskRequest request) {
        try {
            TaskResponse response = taskService.createTask(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskResponse>> getMyTasks() {
        return ResponseEntity.ok(taskService.getMyTasks());
    }

    @GetMapping("/assigned-by-me")
    public ResponseEntity<List<TaskResponse>> getTasksIAssigned() {
        return ResponseEntity.ok(taskService.getTasksIAssigned());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getTasksByProject(@PathVariable String projectId) {
        try {
            return ResponseEntity.ok(taskService.getTasksByProject(projectId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        try {
            return ResponseEntity.ok(taskService.updateTaskStatus(taskId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable String taskId) {
        try {
            return ResponseEntity.ok(taskService.deleteTask(taskId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}