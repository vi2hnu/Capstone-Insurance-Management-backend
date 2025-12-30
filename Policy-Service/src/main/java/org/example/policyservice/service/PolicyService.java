package org.example.policyservice.service;

import org.example.policyservice.dto.CoverageChangeDTO;
import org.example.policyservice.dto.PolicyUserDTO;
import org.example.policyservice.dto.PolicyEnrollDTO;
import org.example.policyservice.model.entity.Policy;

import java.util.List;

public interface PolicyService {
    Policy enrollUser(PolicyEnrollDTO request);
    void cancelPolicy(PolicyUserDTO request);
    Policy renewPolicy(PolicyUserDTO request);
    List<Policy> viewAllRegisteredPolicies(String userId);
    Policy changeCoverage(CoverageChangeDTO request);
    List<Policy> getAllAgentEnrolledPolicies(String agentId);
}
