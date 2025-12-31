    package org.example.claimsservice.model.entity;

    import jakarta.persistence.*;
    import lombok.Data;
    import org.example.claimsservice.model.enums.ReviewStatus;
    import org.example.claimsservice.model.enums.ReviewerRole;

    import java.time.LocalDateTime;

    @Data
    @Entity
    public class ClaimReview {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        @Enumerated(EnumType.STRING)
        ReviewerRole reviewerRole;
        String userId;

        @Enumerated(EnumType.STRING)
        ReviewStatus reviewStatus;

        LocalDateTime reviewDate;
        String comments;

        public ClaimReview() {

        }

        public ClaimReview(ReviewerRole reviewerRole, String userId, ReviewStatus reviewStatus,String comments) {
            this.reviewerRole = reviewerRole;
            this.userId = userId;
            this.reviewStatus = reviewStatus;
            this.reviewDate = LocalDateTime.now();
            this.comments = comments;
        }
    }
