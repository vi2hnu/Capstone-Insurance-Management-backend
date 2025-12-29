package org.example.policyservice.service;

import org.example.policyservice.dto.PolicyUserDTO;
import org.example.policyservice.dto.PolicyEnrollDTO;
import org.example.policyservice.model.entity.PolicyUser;

import java.util.List;

public interface PolicyService {
    PolicyUser enrollUser(PolicyEnrollDTO request);
    void cancelPolicy(PolicyUserDTO request);
    PolicyUser renewPolicy(PolicyUserDTO request);
    List<PolicyUser> viewAllRegisteredPolicies(String username);
}
