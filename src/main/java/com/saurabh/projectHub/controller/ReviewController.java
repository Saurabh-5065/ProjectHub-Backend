package com.saurabh.projectHub.controller;

import com.saurabh.projectHub.dto.ReviewDecisionRequest;
import com.saurabh.projectHub.dto.ReviewResponse;
import com.saurabh.projectHub.dto.SubmitReviewRequest;
import com.saurabh.projectHub.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Assignee submits task for review
    @PostMapping("/submit")
    public ResponseEntity<?> submitForReview(
            @Valid @RequestBody SubmitReviewRequest request) {
        try {
            ReviewResponse response = reviewService.submitForReview(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Team lead gets all pending reviews
    @GetMapping("/pending")
    public ResponseEntity<List<ReviewResponse>> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    // Team lead gets all their reviews (all statuses)
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllMyReviews() {
        return ResponseEntity.ok(reviewService.getAllMyReviews());
    }

    // Team lead approves or rejects
    @PostMapping("/decide")
    public ResponseEntity<?> makeDecision(
            @Valid @RequestBody ReviewDecisionRequest request) {
        try {
            ReviewResponse response = reviewService.makeDecision(request);
            if (response == null) {
                // Rejected — review deleted, task sent back to In Progress
                return ResponseEntity.ok("Task rejected and sent back for rework");
            }
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}