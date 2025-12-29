package org.example.policyservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;
import org.example.policyservice.model.enums.Status;

import java.time.LocalDate;
@Data
@Entity
public class PolicyUser {
    @Id
    Long id;
    Long planId;
    String userId;
    LocalDate startDate;
    LocalDate endDate;

    @Enumerated(EnumType.STRING)
    Status status;
    Double remainingCoverage;
}
