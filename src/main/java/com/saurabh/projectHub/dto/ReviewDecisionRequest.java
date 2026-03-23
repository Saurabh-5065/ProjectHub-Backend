
package com.saurabh.projectHub.dto;

import com.saurabh.projectHub.model.Review.ReviewStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDecisionRequest {

    @NotBlank(message = "Review ID is required")
    private String reviewId;

    @NotNull(message = "Decision is required")
    private ReviewStatus decision;  // APPROVED or REJECTED

    private String comments;        // optional feedback from reviewer
}