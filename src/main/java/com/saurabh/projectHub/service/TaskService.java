package com.saurabh.projectHub.service;

import com.saurabh.projectHub.dto.CreateTaskRequest;
import com.saurabh.projectHub.dto.TaskResponse;
import com.saurabh.projectHub.dto.UpdateTaskStatusRequest;
import com.saurabh.projectHub.model.Project;
import com.saurabh.projectHub.model.Task;
import com.saurabh.projectHub.model.Task.TaskStatus;
import com.saurabh.projectHub.model.User;
import com.saurabh.projectHub.repository.ProjectRepository;
import com.saurabh.projectHub.repository.TaskRepository;
import com.saurabh.projectHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // ── Create Task (team lead assigns to a member) ───────────────────────────
    public TaskResponse createTask(CreateTaskRequest request) {

        // 1. Get logged-in user as assignor
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User assignor = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Validate project exists
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // 3. Only team lead can assign tasks
        if (!project.getTeamLead().getId().equals(assignor.getId())) {
            throw new RuntimeException("Only the team lead can assign tasks");
        }

        // 4. Resolve assignee by username (frontend sends username)
        User assignedTo = userRepository.findByUsername(request.getAssignedToUsername())
                .orElseThrow(() -> new RuntimeException(
                        "User '" + request.getAssignedToUsername() + "' not found"));

        // 5. Assignee must be a project member or the team lead themselves
        boolean isTeamLead = project.getTeamLead().getId().equals(assignedTo.getId());
        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getId().equals(assignedTo.getId()));

        if (!isTeamLead && !isMember) {
            throw new RuntimeException(
                    request.getAssignedToUsername() + " is not a member of this project");
        }

        // 6. Build and save task
        Task task = new Task();
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setProject(project);
        task.setAssignedTo(assignedTo);
        task.setAssignor(assignor);

        Task saved = taskRepository.save(task);
        return mapToResponse(saved);
    }

    // ── Get My Tasks (tasks assigned to logged-in user) ───────────────────────
    public List<TaskResponse> getMyTasks() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository
                .findByAssignedToIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Tasks I Assigned (assignor view — tasks in review) ────────────────
    public List<TaskResponse> getTasksIAssigned() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository
                .findByAssignorIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Tasks for a Project ───────────────────────────────────────────────
    public List<TaskResponse> getTasksByProject(String projectId) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Only team lead or members can view project tasks
        boolean isTeamLead = project.getTeamLead().getId().equals(user.getId());
        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getId().equals(user.getId()));

        if (!isTeamLead && !isMember) {
            throw new RuntimeException("You are not authorized to view tasks for this project");
        }

        return taskRepository
                .findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Update Task Status (assignee updates their own task) ──────────────────
    public TaskResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Only assignee or assignor can update status
        boolean isAssignee = task.getAssignedTo().getId().equals(user.getId());
        boolean isAssignor = task.getAssignor().getId().equals(user.getId());

        if (!isAssignee && !isAssignor) {
            throw new RuntimeException("You are not authorized to update this task");
        }

        // If marking completed, set the completed flag too
        if (request.getStatus() == TaskStatus.COMPLETED) {
            task.setCompleted(true);
        } else {
            task.setCompleted(false);
        }

        task.setStatus(request.getStatus());
        Task updated = taskRepository.save(task);
        return mapToResponse(updated);
    }

    // ── Delete Task (team lead only) ──────────────────────────────────────────
    public String deleteTask(String taskId) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getAssignor().getId().equals(user.getId())) {
            throw new RuntimeException("Only the task assignor can delete this task");
        }

        taskRepository.delete(task);
        return "Task deleted successfully";
    }

    // ── Mapper ────────────────────────────────────────────────────────────────
    private TaskResponse mapToResponse(Task task) {
        TaskResponse res = new TaskResponse();
        res.setId(task.getId());
        res.setTitle(task.getTitle());
        res.setDescription(task.getDescription());
        res.setDueDate(task.getDueDate());
        res.setPriority(task.getPriority());
        res.setCompleted(task.isCompleted());
        res.setStatus(task.getStatus());
        res.setProjectId(task.getProject().getId());
        res.setProjectName(task.getProject().getName());
        res.setAssignedToId(task.getAssignedTo().getId());
        res.setAssignedToUsername(task.getAssignedTo().getUsername());
        res.setAssignorId(task.getAssignor().getId());
        res.setAssignorName(task.getAssignor().getName());
        res.setCreatedAt(task.getCreatedAt());
        res.setUpdatedAt(task.getUpdatedAt());
        return res;
    }
}