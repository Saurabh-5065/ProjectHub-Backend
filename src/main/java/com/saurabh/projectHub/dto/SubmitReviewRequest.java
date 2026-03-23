// SubmitReviewRequest.java  (task assignee submits work for review)
package com.saurabh.projectHub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmitReviewRequest {

    @NotBlank(message = "Task ID is required")
    private String taskId;

    private String comments;    // optional notes from assignee
}