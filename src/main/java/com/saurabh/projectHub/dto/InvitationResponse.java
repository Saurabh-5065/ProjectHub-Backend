// InvitationResponse.java
package com.saurabh.projectHub.dto;

import com.saurabh.projectHub.model.Invitation.InvitationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InvitationResponse {
    private String id;
    private String projectId;
    private String projectName;
    private String projectDescription;
    private String senderName;
    private String receiverUsername;
    private InvitationStatus status;
    private LocalDateTime createdAt;
}