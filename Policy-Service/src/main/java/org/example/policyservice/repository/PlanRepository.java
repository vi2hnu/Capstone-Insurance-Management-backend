package org.example.policyservice.repository;

import org.example.policyservice.model.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanRepository extends JpaRepository<Plan,Long> {
    boolean existsPlanByName(String name);
}
