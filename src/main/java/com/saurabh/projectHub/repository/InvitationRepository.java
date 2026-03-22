package com.saurabh.projectHub.repository;

import com.saurabh.projectHub.model.Invitation;
import com.saurabh.projectHub.model.Invitation.InvitationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends MongoRepository<Invitation, String> {

    // All pending invitations for a receiver
    List<Invitation> findByReceiverIdAndStatus(String receiverId, InvitationStatus status);

    // Check if invitation already exists (prevent duplicates)
    boolean existsByProjectIdAndReceiverIdAndStatus(
            String projectId, String receiverId, InvitationStatus status
    );

    // Find specific invitation for authorization check
    Optional<Invitation> findByIdAndReceiverId(String id, String receiverId);
}