package org.example.policyservice.service;

import org.example.policyservice.dto.PlanDTO;
import org.example.policyservice.model.entity.Plan;

import java.util.List;

public interface PlanService {
    List<Plan> getAllPlans();
    Plan addPlan(PlanDTO request);
}
