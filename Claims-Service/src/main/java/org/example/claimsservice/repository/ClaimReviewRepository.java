package org.example.claimsservice.repository;

import org.example.claimsservice.model.entity.ClaimReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimReviewRepository extends JpaRepository<ClaimReview, Long> {
}
