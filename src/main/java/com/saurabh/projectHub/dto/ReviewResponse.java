// ReviewResponse.java
package com.saurabh.projectHub.dto;

import com.saurabh.projectHub.model.Review.ReviewStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private String id;
    private String taskId;
    private String taskTitle;
    private String reviewerId;
    private String reviewerName;
    private ReviewStatus status;
    private String comments;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}