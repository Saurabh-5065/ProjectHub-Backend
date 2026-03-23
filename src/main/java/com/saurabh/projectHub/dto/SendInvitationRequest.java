package com.saurabh.projectHub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class SendInvitationRequest {

    @NotBlank(message = "Project ID is required")
    private String projectId;

    @NotEmpty(message = "At least one username is required")
    private List<@NotBlank String> usernames;   // frontend sends usernames
}