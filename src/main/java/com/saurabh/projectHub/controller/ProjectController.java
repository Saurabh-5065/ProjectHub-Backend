package com.saurabh.projectHub.controller;

import com.saurabh.projectHub.dto.CreateProjectRequest;
import com.saurabh.projectHub.dto.ProjectResponse;
import com.saurabh.projectHub.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/createProject")
    public ResponseEntity<?> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        try {
            ProjectResponse response = projectService.createProject(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getProject")
    public ResponseEntity<List<ProjectResponse>> getMyProjects() {
        return ResponseEntity.ok(projectService.getMyProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable String projectId) {
        try {
            return ResponseEntity.ok(projectService.getProjectById(projectId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateProject(
            @PathVariable String projectId,
            @Valid @RequestBody CreateProjectRequest request) {
        try {
            return ResponseEntity.ok(projectService.updateProject(projectId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable String projectId) {
        try {
            return ResponseEntity.ok(projectService.deleteProject(projectId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/{projectId}/members/{memberUsername}")
    public ResponseEntity<?> removeMember(
            @PathVariable String projectId,
            @PathVariable String memberUsername) {
        try {
            return ResponseEntity.ok(projectService.removeMember(projectId, memberUsername));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}