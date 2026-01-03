package org.example.policyservice.repository;

import java.time.LocalDate;
import java.util.List;

import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.model.entity.Policy;
import org.example.policyservice.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PolicyUserRepository extends JpaRepository<Policy,Long> {
    boolean existsPolicyUserByUserIdAndPlan(String userId, Plan plan);
    List<Policy> findByUserId(String userId);
    List<Policy> findByAgentId(String agentId);
    List<Policy> findByEndDateAndStatus(LocalDate endDate,Status status);
    Policy findByUserIdAndPlan(String userId, Plan plan);
}
