package org.example.policyservice.service;

import java.util.List;

import org.example.policyservice.dto.PlanDTO;
import org.example.policyservice.model.entity.Plan;

public interface PlanService {
    List<Plan> getAllPlans();
    Plan addPlan(PlanDTO request);
    Plan getPlan(Long id);
}
