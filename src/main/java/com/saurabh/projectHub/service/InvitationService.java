package com.saurabh.projectHub.service;

import com.saurabh.projectHub.dto.InvitationResponse;
import com.saurabh.projectHub.dto.RespondToInvitationRequest;
import com.saurabh.projectHub.dto.SendInvitationRequest;
import com.saurabh.projectHub.model.Invitation;
import com.saurabh.projectHub.model.Invitation.InvitationStatus;
import com.saurabh.projectHub.model.Project;
import com.saurabh.projectHub.model.User;
import com.saurabh.projectHub.repository.InvitationRepository;
import com.saurabh.projectHub.repository.ProjectRepository;
import com.saurabh.projectHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // ── Send invitations by username ──────────────────────────────────────────
    public List<InvitationResponse> sendInvitations(SendInvitationRequest request) {

        // Get logged-in sender
        String senderUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Validate project exists and sender is the team lead
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getTeamLead().getId().equals(sender.getId())) {
            throw new RuntimeException("Only the team lead can send invitations");
        }

        List<InvitationResponse> responses = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (String username : request.getUsernames()) {

            // Cannot invite yourself
            if (username.equals(senderUsername)) {
                skipped.add(username + " (cannot invite yourself)");
                continue;
            }

            // Find receiver by username — frontend sends username directly
            User receiver = userRepository.findByUsername(username).orElse(null);
            if (receiver == null) {
                skipped.add(username + " (user not found)");
                continue;
            }

            // Skip if already a member
            boolean alreadyMember = project.getMembers().stream()
                    .anyMatch(m -> m.getId().equals(receiver.getId()));
            if (alreadyMember) {
                skipped.add(username + " (already a member)");
                continue;
            }

            // Skip if pending invitation already exists
            boolean alreadyInvited = invitationRepository
                    .existsByProjectIdAndReceiverIdAndStatus(
                            project.getId(), receiver.getId(), InvitationStatus.PENDING);
            if (alreadyInvited) {
                skipped.add(username + " (invitation already pending)");
                continue;
            }

            // Create and save invitation
            Invitation invitation = new Invitation();
            invitation.setProject(project);
            invitation.setSender(sender);
            invitation.setReceiver(receiver);

            Invitation saved = invitationRepository.save(invitation);
            responses.add(mapToResponse(saved));
        }

        if (!skipped.isEmpty()) {
            System.out.println("Skipped invitations: " + skipped); // log or handle as needed
        }

        return responses;
    }

    // ── Get all pending invitations for logged-in user ────────────────────────
    public List<InvitationResponse> getPendingInvitations() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User receiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return invitationRepository
                .findByReceiverIdAndStatus(receiver.getId(), InvitationStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Accept or Reject invitation ───────────────────────────────────────────
    public String respondToInvitation(RespondToInvitationRequest request) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User receiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch invitation and verify it belongs to this user
        Invitation invitation = invitationRepository
                .findByIdAndReceiverId(request.getInvitationId(), receiver.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Invitation not found or you are not authorized"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("Invitation has already been responded to");
        }

        if ("accepted".equals(request.getResponse())) {

            // Add user to project members (prevent duplicates with addToSet equivalent)
            Project project = invitation.getProject();
            boolean alreadyMember = project.getMembers().stream()
                    .anyMatch(m -> m.getId().equals(receiver.getId()));

            if (!alreadyMember) {
                project.getMembers().add(receiver);
                projectRepository.save(project);
            }

            // Delete invitation after accepting (mirrors your JS deleteOne())
            invitationRepository.delete(invitation);
            return "You have joined the project: " + project.getName();

        } else {
            // Delete invitation after rejecting too
            invitationRepository.delete(invitation);
            return "You have rejected the invitation.";
        }
    }

    // ── Mapper ────────────────────────────────────────────────────────────────
    private InvitationResponse mapToResponse(Invitation invitation) {
        InvitationResponse res = new InvitationResponse();
        res.setId(invitation.getId());
        res.setProjectId(invitation.getProject().getId());
        res.setProjectName(invitation.getProject().getName());
        res.setProjectDescription(invitation.getProject().getDescription());
        res.setSenderName(invitation.getSender().getName());
        res.setReceiverUsername(invitation.getReceiver().getUsername());
        res.setStatus(invitation.getStatus());
        res.setCreatedAt(invitation.getCreatedAt());
        return res;
    }
}