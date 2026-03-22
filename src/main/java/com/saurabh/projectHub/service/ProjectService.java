package com.saurabh.projectHub.service;

import com.saurabh.projectHub.dto.CreateProjectRequest;
import com.saurabh.projectHub.dto.ProjectResponse;
import com.saurabh.projectHub.model.Project;
import com.saurabh.projectHub.model.User;
import com.saurabh.projectHub.repository.ProjectRepository;
import com.saurabh.projectHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse createProject(CreateProjectRequest request) {

        // 1. Get the currently logged-in user from JWT context
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User teamLead = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // 2. Resolve member IDs to User objects (if any provided)
        List<User> members = new ArrayList<>();
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            members = userRepository.findAllById(request.getMemberIds());
        }

        // 3. Build the Project document
        Project project = new Project();
        project.setName(request.getName());
        project.setPriority(request.getPriority());
        project.setDescription(request.getDescription());
        project.setDueDate(request.getDueDate());
        project.setProgress(request.getProgress());
        project.setStatus(request.getStatus());
        project.setTeamLead(teamLead);
        project.setMembers(members);

        Project saved = projectRepository.save(project);

        return mapToResponse(saved);
    }
    private ProjectResponse mapToResponse(Project project) {
        ProjectResponse res = new ProjectResponse();
        res.setId(project.getId());
        res.setName(project.getName());
        return res;
    }
}