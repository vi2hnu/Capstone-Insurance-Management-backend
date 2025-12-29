package org.example.policyservice.repository;

import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.model.entity.PolicyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyUserRepository extends JpaRepository<PolicyUser,Long> {
    boolean existsPolicyUserByUserIdAndPlan(String userId, Plan plan);
    List<PolicyUser> findByUserId(String userId);
}
