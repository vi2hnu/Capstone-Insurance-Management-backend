package org.example.policyservice.repository;

import java.time.LocalDate;
import java.util.List;

import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.model.entity.Policy;
import org.example.policyservice.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PolicyUserRepository extends JpaRepository<Policy,Long> {
    boolean existsPolicyUserByUserIdAndPlan(String userId, Plan plan);
    List<Policy> findByUserId(String userId);
    List<Policy> findByAgentId(String agentId);
    List<Policy> findByEndDateAndStatus(LocalDate endDate,Status status);
    Policy findByUserIdAndPlan(String userId, Plan plan);

    @Query("SELECT p.plan, COUNT(p.id) FROM Policy p " +
           "WHERE p.startDate >= :fromDate AND p.startDate <= :toDate " +
           "GROUP BY p.plan " +
           "ORDER BY COUNT(p.id) DESC")
    List<Object[]> findPlanCounts(@Param("fromDate") LocalDate fromDate,
                                  @Param("toDate") LocalDate toDate);

    @Query("SELECT p.status, COUNT(p.id) FROM Policy p GROUP BY p.status")
    List<Object[]> countPoliciesByStatus();                              
}
