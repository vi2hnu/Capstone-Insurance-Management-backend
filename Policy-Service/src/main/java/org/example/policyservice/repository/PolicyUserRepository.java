package org.example.policyservice.repository;

import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.model.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyUserRepository extends JpaRepository<Policy,Long> {
    boolean existsPolicyUserByUserIdAndPlan(String userId, Plan plan);
    List<Policy> findByUserId(String userId);
    List<Policy> findByAgentId(String agentId);
}
