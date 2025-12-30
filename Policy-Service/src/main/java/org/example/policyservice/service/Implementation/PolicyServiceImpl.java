package org.example.policyservice.service.Implementation;

import lombok.extern.slf4j.Slf4j;
import org.example.policyservice.dto.CoverageChangeDTO;
import org.example.policyservice.dto.PolicyUserDTO;
import org.example.policyservice.dto.PolicyEnrollDTO;
import org.example.policyservice.exception.*;
import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.model.entity.Policy;
import org.example.policyservice.model.enums.Status;
import org.example.policyservice.repository.PlanRepository;
import org.example.policyservice.repository.PolicyUserRepository;
import org.example.policyservice.service.PolicyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PolicyServiceImpl implements PolicyService {

    private final PlanRepository planRepository;
    private final PolicyUserRepository policyRepository;

    public PolicyServiceImpl(PlanRepository planRepository ,PolicyUserRepository policyRepository){
        this.planRepository = planRepository;
        this.policyRepository = policyRepository;
    }

    @Override
    public Policy enrollUser(PolicyEnrollDTO request) {
        if(!planRepository.existsPlanById(request.planId())){
            log.error("Plan does not exists for {}",request.planId());
            throw new PlanNotFoundException("Plan does not exists");
        }
        //after feign connection check if user exists

        Plan plan = planRepository.findPlanById(request.planId());
        if(policyRepository.existsPolicyUserByUserIdAndPlan(request.userId(),plan)){
            log.error("User Already enrolled in plan");
            throw new UserAlreadyEnrolledException("User Already enrolled in plan");
        }

        Policy policy = new Policy(plan,request.userId(),LocalDate.now(),
                LocalDate.now().plusMonths(plan.getDuration()), Status.ACTIVE,plan.getCoverageAmount(), 0);

        // send email to user
        policy.setAgentId(request.agentId());
        return policyRepository.save(policy);
    }

    @Override
    public void cancelPolicy(PolicyUserDTO request) {
        Policy policy = policyRepository.findById(request.policyId())
                .orElseThrow(() -> new PolicyNotFoundException("Policy does not exist: " + request.policyId()));

        if(!Objects.equals(request.userId(), policy.getUserId())){
            throw new UserNotEnrolledException("User does not match user");
        }

        if(!Objects.equals(request.agentId(), policy.getAgentId())){
            log.error("Policy does not match agent id");
            throw new PolicyNotEnrolledByAgentException("Policy was not enrolled by agent");
        }

        policy.setStatus(Status.CANCELLED);
        //send email to user

        policyRepository.save(policy);
    }

    @Override
    public Policy renewPolicy(PolicyUserDTO request) {
        Policy policy = policyRepository.findById(request.policyId())
                .orElseThrow(() -> new PolicyNotFoundException("Policy does not exist: " + request.policyId()));

        if(!Objects.equals(request.userId(), policy.getUserId())){
            throw new UserNotEnrolledException("User does not match user");
        }

        if(policy.getStatus()==Status.CANCELLED){
            log.info("user not enrolled in policy");
            throw new UserNotEnrolledException("User not enrolled in policy");
        }

        if(!Objects.equals(request.agentId(), policy.getAgentId())){
            log.error("Policy does not match agent id {}", policy.getAgentId());
            throw new PolicyNotEnrolledByAgentException("Policy was not enrolled by agent");
        }

        policy.setRenewalCounter(policy.getRenewalCounter()+1);
        policy.setEndDate(policy.getEndDate().plusMonths(policy.getPlan().getDuration()));
        //send email

        return policyRepository.save(policy);
    }

    @Override
    public List<Policy> viewAllRegisteredPolicies(String userId) {
        return policyRepository.findByUserId(userId);
    }

    @Override
    public Policy changeCoverage(CoverageChangeDTO request) {
        Policy policy = policyRepository.findById(request.policyId())
                .orElseThrow(()->new PolicyNotFoundException("User not enrolled in policy"));

        if(!Objects.equals(policy.getAgentId(), request.agentId())){
            throw new PolicyNotEnrolledByAgentException("Policy was not enrolled by agent");
        }

        if(policy.getStatus()!=Status.ACTIVE){
            throw new UserNotEnrolledException("User not enrolled in policy");
        }

        policy.setRemainingCoverage(policy.getRemainingCoverage()- request.claimedAmount());
        return policyRepository.save(policy);
    }

    @Override
    public List<Policy> getAllAgentEnrolledPolicies(String agentId) {
        return policyRepository.findByAgentId(agentId);
    }

    @Override
    public Policy getPolicyById(Long policyId) {
        return policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException("Policy does not exist: " + policyId));
    }


}
