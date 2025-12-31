package org.example.claimsservice.repository;

import org.example.claimsservice.model.entity.ClaimReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimReviewRepository extends JpaRepository<ClaimReview, Long> {
}
