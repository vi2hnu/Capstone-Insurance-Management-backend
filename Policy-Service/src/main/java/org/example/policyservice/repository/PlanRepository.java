package org.example.policyservice.repository;

import org.example.policyservice.model.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan,Long> {
    boolean existsPlanByName(String name);
    boolean existsPlanById(Long id);
    Plan findPlanById(Long id);
}
