package com.saurabh.projectHub.repository;

import com.saurabh.projectHub.model.Review;
import com.saurabh.projectHub.model.Review.ReviewStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review, String> {

    // All reviews for a reviewer (team lead)
    List<Review> findByReviewerIdOrderByCreatedAtDesc(String reviewerId);

    // All reviews by status for a reviewer
    List<Review> findByReviewerIdAndStatusOrderByCreatedAtDesc(
            String reviewerId, ReviewStatus status
    );

    // Get review for a specific task
    Optional<Review> findByTaskId(String taskId);

    // Check if review already exists for a task
    boolean existsByTaskId(String taskId);
}