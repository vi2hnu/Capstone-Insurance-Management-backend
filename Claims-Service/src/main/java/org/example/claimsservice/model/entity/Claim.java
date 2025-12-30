package org.example.claimsservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.claimsservice.model.enums.ClaimStage;
import org.example.claimsservice.model.enums.ClaimStatus;

import java.time.LocalDateTime;

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

}
