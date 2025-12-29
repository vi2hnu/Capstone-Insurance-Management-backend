package org.example.policyservice.service.Implementation;

import lombok.extern.slf4j.Slf4j;
import org.example.policyservice.dto.PolicyUserDTO;
import org.example.policyservice.dto.PolicyEnrollDTO;
import org.example.policyservice.exception.PlanNotFoundException;
import org.example.policyservice.exception.PolicyNotFoundException;
import org.example.policyservice.exception.UserAlreadyEnrolledException;
import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.model.entity.PolicyUser;
import org.example.policyservice.model.enums.Status;
import org.example.policyservice.repository.PlanRepository;
import org.example.policyservice.repository.PolicyUserRepository;
import org.example.policyservice.service.PolicyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class PolicyServiceImpl implements PolicyService {

    private final PlanRepository planRepository;
    private final PolicyUserRepository policyUserRepository;

    public PolicyServiceImpl(PlanRepository planRepository ,PolicyUserRepository policyUserRepository){
        this.planRepository = planRepository;
        this.policyUserRepository = policyUserRepository;
    }

    @Override
    public PolicyUser enrollUser(PolicyEnrollDTO request) {
        if(!planRepository.existsPlanById(request.planId())){
            log.error("Plan does not exists for {}",request.planId());
            throw new PlanNotFoundException("Plan does not exists");
        }
        //after feign connection check if user exists

        Plan plan = planRepository.findPlanById(request.planId());
        if(policyUserRepository.existsPolicyUserByUserIdAndPlan(request.userId(),plan)){
            log.error("User Already enrolled in plan");
            throw new UserAlreadyEnrolledException("User Already enrolled in plan");
        }

        PolicyUser policy = new PolicyUser(plan,request.userId(),LocalDate.now(),
                LocalDate.now().plusMonths(plan.getDuration()), Status.ACTIVE,plan.getCoverageAmount(), 0);

        //after notification service send email to user
        return policyUserRepository.save(policy);
    }

    @Override
    public void cancelPolicy(PolicyUserDTO request) {
        PolicyUser policy = policyUserRepository.findById(request.policyId())
                .orElseThrow(() -> new PolicyNotFoundException("Policy does not exist: " + request.policyId()));

        policy.setStatus(Status.CANCELLED);
        //send email to user

        policyUserRepository.save(policy);
    }

    @Override
    public PolicyUser renewPolicy(PolicyUserDTO request) {
        PolicyUser policy = policyUserRepository.findById(request.policyId())
                .orElseThrow(() -> new PolicyNotFoundException("Policy does not exist: " + request.policyId()));

        policy.setRenewalCounter(policy.getRenewalCounter()+1);
        policy.setEndDate(policy.getEndDate().plusMonths(policy.getPlan().getDuration()));
        //send email

        return policyUserRepository.save(policy);
    }

    @Override
    public List<PolicyUser> viewAllRegisteredPolicies(String username) {
        return policyUserRepository.findByUserId(username);
    }
}
