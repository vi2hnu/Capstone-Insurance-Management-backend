package org.example.policyservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.policyservice.model.enums.Status;

import java.time.LocalDate;
@Data
@Entity
public class PolicyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    Plan plan;
    String userId;
    LocalDate startDate;
    LocalDate endDate;

    @Enumerated(EnumType.STRING)
    Status status;
    Double remainingCoverage;
    Integer renewalCounter;
    String agentId;

    public PolicyUser(Plan plan, String userId, LocalDate startDate, LocalDate endDate,
            Status status, Double remainingCoverage,Integer renewalCounter){
        this.plan = plan;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.remainingCoverage = remainingCoverage;
        this.renewalCounter = renewalCounter;
    }

    public PolicyUser() {

    }
}
