package com.saurabh.projectHub.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Data
@Document(collection = "invitations")
public class Invitation {

    @Id
    private String id;

    @DBRef
    private Project project;

    @DBRef
    private User sender;

    @DBRef
    private User receiver;

    private InvitationStatus status = InvitationStatus.PENDING;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum InvitationStatus {
        PENDING, ACCEPTED, REJECTED
    }
}