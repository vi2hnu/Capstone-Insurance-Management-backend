package org.example.claimsservice.model.entity;

import java.time.LocalDateTime;

import org.example.claimsservice.model.enums.ClaimStage;
import org.example.claimsservice.model.enums.ClaimStatus;
import org.example.claimsservice.model.enums.ClaimSubmissionEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long policyId;
    String userId;
    Long hospitalId;
    Double requestedAmount;
    String supportingDocument;  //cloudinary url
    LocalDateTime claimRequestDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "provider_review_id")
    ClaimReview providerReview;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "officer_review_id")
    ClaimReview claimsOfficerReview;

    @Enumerated(EnumType.STRING)
    ClaimStatus status;

    @Enumerated(EnumType.STRING)
    ClaimStage stage;

    @Enumerated(EnumType.STRING)
    ClaimSubmissionEntity submittedBy;

    public Claim(Long policyId, String userId, Long hospitalId,Double requestedAmount, String supportingDocument){
        this.policyId = policyId;
        this.userId = userId;
        this.hospitalId = hospitalId;
        this.requestedAmount = requestedAmount;
        this.supportingDocument = supportingDocument;
        this.claimRequestDate = LocalDateTime.now();
        this.status = ClaimStatus.SUBMITTED;
        this.stage  = ClaimStage.PROVIDER;
        this.submittedBy = ClaimSubmissionEntity.USER;
    }

    public Claim(){
        
    }

}
