package com.saurabh.projectHub.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Data
@Document(collection = "reviews")
public class Review {

    @Id
    private String id;

    @DBRef
    private Task task;

    @DBRef
    private User reviewer;          // team lead who reviews

    private ReviewStatus status = ReviewStatus.PENDING;

    private String comments;

    private LocalDateTime submittedAt = LocalDateTime.now();

    private LocalDateTime reviewedAt; // set when approved/rejected

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum ReviewStatus {
        PENDING, APPROVED, REJECTED
    }
}