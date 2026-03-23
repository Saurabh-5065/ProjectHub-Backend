package com.saurabh.projectHub.service;

import com.saurabh.projectHub.dto.CreateProjectRequest;
import com.saurabh.projectHub.dto.ProjectResponse;
import com.saurabh.projectHub.dto.SendInvitationRequest;
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
    private final InvitationService invitationService;

    // ── Create Project ────────────────────────────────────────────────────────
    public ProjectResponse createProject(CreateProjectRequest request) {

        // 1. Get logged-in user as team lead
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User teamLead = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // 2. Build project — members always starts empty
        Project project = new Project();
        project.setName(request.getName());
        project.setPriority(request.getPriority());
        project.setDescription(request.getDescription());
        project.setDueDate(request.getDueDate());
        project.setProgress(request.getProgress());
        project.setStatus(request.getStatus());
        project.setTeamLead(teamLead);
        project.setMembers(new ArrayList<>());  // no direct members — invitations only

        Project saved = projectRepository.save(project);

        // 3. Send invitations if usernames were provided at creation time
        if (request.getInviteUsernames() != null
                && !request.getInviteUsernames().isEmpty()) {

            SendInvitationRequest inviteRequest = new SendInvitationRequest();
            inviteRequest.setProjectId(saved.getId());
            inviteRequest.setUsernames(request.getInviteUsernames());
            invitationService.sendInvitations(inviteRequest);
        }

        return mapToResponse(saved);
    }

    // ── Get All Projects for Logged-in User ───────────────────────────────────
    // Returns projects where user is team lead OR a member
    public List<ProjectResponse> getMyProjects() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Projects where user is team lead
        List<Project> ledProjects = projectRepository.findByTeamLeadId(user.getId());

        // Projects where user is a member
        List<Project> memberProjects = projectRepository.findByMembersId(user.getId());

        // Merge both lists, avoiding duplicates
        List<Project> allProjects = new ArrayList<>(ledProjects);
        memberProjects.stream()
                .filter(p -> ledProjects.stream()
                        .noneMatch(lp -> lp.getId().equals(p.getId())))
                .forEach(allProjects::add);

        return allProjects.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Single Project by ID ──────────────────────────────────────────────
    public ProjectResponse getProjectById(String projectId) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Only team lead or members can view the project
        boolean isTeamLead = project.getTeamLead().getId().equals(user.getId());
        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getId().equals(user.getId()));

        if (!isTeamLead && !isMember) {
            throw new RuntimeException("You are not authorized to view this project");
        }

        return mapToResponse(project);
    }

    // ── Update Project ────────────────────────────────────────────────────────
    // Only the team lead can update
    public ProjectResponse updateProject(String projectId, CreateProjectRequest request) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getTeamLead().getId().equals(user.getId())) {
            throw new RuntimeException("Only the team lead can update this project");
        }

        // Update only provided fields
        if (request.getName() != null)
            project.setName(request.getName());

        if (request.getPriority() != null)
            project.setPriority(request.getPriority());

        if (request.getDescription() != null)
            project.setDescription(request.getDescription());

        if (request.getDueDate() != null)
            project.setDueDate(request.getDueDate());

        project.setProgress(request.getProgress());

        if (request.getStatus() != null)
            project.setStatus(request.getStatus());

        // Send new invitations if provided during update
        if (request.getInviteUsernames() != null
                && !request.getInviteUsernames().isEmpty()) {

            SendInvitationRequest inviteRequest = new SendInvitationRequest();
            inviteRequest.setProjectId(project.getId());
            inviteRequest.setUsernames(request.getInviteUsernames());
            invitationService.sendInvitations(inviteRequest);
        }

        Project updated = projectRepository.save(project);
        return mapToResponse(updated);
    }

    // ── Delete Project ────────────────────────────────────────────────────────
    // Only the team lead can delete
    public String deleteProject(String projectId) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getTeamLead().getId().equals(user.getId())) {
            throw new RuntimeException("Only the team lead can delete this project");
        }

        projectRepository.delete(project);
        return "Project deleted successfully";
    }

    // ── Remove a Member ───────────────────────────────────────────────────────
    // Team lead can remove any member, or a member can remove themselves
    public ProjectResponse removeMember(String projectId, String memberUsername) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        boolean isTeamLead = project.getTeamLead().getId().equals(requester.getId());
        boolean isSelf = username.equals(memberUsername);

        if (!isTeamLead && !isSelf) {
            throw new RuntimeException(
                    "Only the team lead can remove members, or you can remove yourself");
        }

        // Remove the member from the list
        boolean removed = project.getMembers()
                .removeIf(m -> m.getUsername().equals(memberUsername));

        if (!removed) {
            throw new RuntimeException(memberUsername + " is not a member of this project");
        }

        Project updated = projectRepository.save(project);
        return mapToResponse(updated);
    }

    // ── Mapper: Project → ProjectResponse ─────────────────────────────────────
    private ProjectResponse mapToResponse(Project project) {
        ProjectResponse res = new ProjectResponse();
        res.setId(project.getId());
        res.setName(project.getName());
        return res;
    }
}