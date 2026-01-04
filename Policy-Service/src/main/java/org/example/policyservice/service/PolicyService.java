package org.example.policyservice.service;

import java.util.List;

import org.example.policyservice.dto.CoverageChangeDTO;
import org.example.policyservice.dto.PlanCountDTO;
import org.example.policyservice.dto.PolicyEnrollDTO;
import org.example.policyservice.dto.PolicyStatusCountDTO;
import org.example.policyservice.dto.PolicyUserDTO;
import org.example.policyservice.model.entity.Policy;

public interface PolicyService {
    Policy enrollUser(PolicyEnrollDTO request);
    void cancelPolicy(PolicyUserDTO request);
    Policy renewPolicy(PolicyUserDTO request);
    List<Policy> viewAllRegisteredPolicies(String userId);
    Policy changeCoverage(CoverageChangeDTO request);
    List<Policy> getAllAgentEnrolledPolicies(String agentId);
    Policy getPolicyById(Long policyId);
    Policy getEnrollment(String userId,Long planId);
    List<PlanCountDTO> getMostPurchasedPlansLastMonth();
    List<PolicyStatusCountDTO> getPolicyCountByStatus();
}
