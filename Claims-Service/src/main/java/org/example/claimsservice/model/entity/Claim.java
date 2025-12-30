package org.example.claimsservice.model.entity;

import java.time.LocalDateTime;

import org.example.claimsservice.model.enums.ClaimStage;
import org.example.claimsservice.model.enums.ClaimStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    Long policyId;
    String userId;
    Long hospitalId;
    Double requestedAmount;
    String supportingDocument;  //cloudinary url
    LocalDateTime claimRequestDate;

    @Enumerated(EnumType.STRING)
    ClaimStatus status;

    @Enumerated(EnumType.STRING)
    ClaimStage stage;

    String rejectionReason;

    public Claim(Long policyId, String userId, Long hospitalId, Double requestedAmount, String supportingDocument) {
        this.policyId = policyId;
        this.userId = userId;
        this.hospitalId = hospitalId;
        this.requestedAmount = requestedAmount;
        this.supportingDocument = supportingDocument;
        this.claimRequestDate = LocalDateTime.now();
        this.status = ClaimStatus.PENDING;
        this.stage = ClaimStage.PROVIDER;
    }

    public Claim() {
        
    }
}
