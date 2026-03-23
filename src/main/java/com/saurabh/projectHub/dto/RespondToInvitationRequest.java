
package com.saurabh.projectHub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RespondToInvitationRequest {

    @NotBlank(message = "Invitation ID is required")
    private String invitationId;

    @NotBlank(message = "Response is required")
    @Pattern(regexp = "accepted|rejected", message = "Response must be 'accepted' or 'rejected'")
    private String response;
}