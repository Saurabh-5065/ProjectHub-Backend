package com.saurabh.projectHub.service;

import com.saurabh.projectHub.dto.ReviewDecisionRequest;
import com.saurabh.projectHub.dto.ReviewResponse;
import com.saurabh.projectHub.dto.SubmitReviewRequest;
import com.saurabh.projectHub.model.Review;
import com.saurabh.projectHub.model.Review.ReviewStatus;
import com.saurabh.projectHub.model.Task;
import com.saurabh.projectHub.model.Task.TaskStatus;
import com.saurabh.projectHub.model.User;
import com.saurabh.projectHub.repository.ReviewRepository;
import com.saurabh.projectHub.repository.TaskRepository;
import com.saurabh.projectHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // ── Submit Task for Review (assignee submits their completed work) ─────────
    public ReviewResponse submitForReview(SubmitReviewRequest request) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User assignee = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Only the assignee can submit their task for review
        if (!task.getAssignedTo().getId().equals(assignee.getId())) {
            throw new RuntimeException("You are not authorized to submit this task for review");
        }

        // Cannot submit if already in review or completed
        if (task.getStatus() == TaskStatus.IN_REVIEW) {
            throw new RuntimeException("Task is already submitted for review");
        }
        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new RuntimeException("Task is already completed");
        }

        // Prevent duplicate review submissions
        if (reviewRepository.existsByTaskId(task.getId())) {
            throw new RuntimeException("A review already exists for this task");
        }

        // Update task status to IN_REVIEW
        task.setStatus(TaskStatus.IN_REVIEW);
        taskRepository.save(task);

        // Create review — reviewer is the assignor (team lead)
        Review review = new Review();
        review.setTask(task);
        review.setReviewer(task.getAssignor());     // team lead reviews
        review.setComments(request.getComments());
        review.setSubmittedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    // ── Get Pending Reviews (team lead sees what needs review) ────────────────
    public List<ReviewResponse> getPendingReviews() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User reviewer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return reviewRepository
                .findByReviewerIdAndStatusOrderByCreatedAtDesc(
                        reviewer.getId(), ReviewStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get All Reviews for Reviewer (all statuses) ───────────────────────────
    public List<ReviewResponse> getAllMyReviews() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User reviewer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return reviewRepository
                .findByReviewerIdOrderByCreatedAtDesc(reviewer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Approve or Reject Review (team lead decides) ──────────────────────────
    public ReviewResponse makeDecision(ReviewDecisionRequest request) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User reviewer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Only the assigned reviewer (team lead) can make a decision
        if (!review.getReviewer().getId().equals(reviewer.getId())) {
            throw new RuntimeException("You are not authorized to review this task");
        }

        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new RuntimeException("This review has already been decided");
        }

        if (request.getDecision() == ReviewStatus.PENDING) {
            throw new RuntimeException("Decision must be APPROVED or REJECTED");
        }

        // Update review
        review.setStatus(request.getDecision());
        review.setReviewedAt(LocalDateTime.now());
        if (request.getComments() != null) {
            review.setComments(request.getComments());
        }

        // Sync task status based on decision
        Task task = review.getTask();
        if (request.getDecision() == ReviewStatus.APPROVED) {
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompleted(true);
        } else {
            // Rejected — send back to In Progress so assignee can rework
            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setCompleted(false);
            // Delete the review so they can resubmit after fixing
            taskRepository.save(task);
            reviewRepository.delete(review);
            return null; // handled below in controller
        }

        taskRepository.save(task);
        Review updated = reviewRepository.save(review);
        return mapToResponse(updated);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────
    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse res = new ReviewResponse();
        res.setId(review.getId());
        res.setTaskId(review.getTask().getId());
        res.setTaskTitle(review.getTask().getTitle());
        res.setReviewerId(review.getReviewer().getId());
        res.setReviewerName(review.getReviewer().getName());
        res.setStatus(review.getStatus());
        res.setComments(review.getComments());
        res.setSubmittedAt(review.getSubmittedAt());
        res.setReviewedAt(review.getReviewedAt());
        res.setCreatedAt(review.getCreatedAt());
        return res;
    }
}