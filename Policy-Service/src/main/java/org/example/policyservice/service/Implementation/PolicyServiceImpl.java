package org.example.policyservice.service.Implementation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.example.policyservice.dto.CoverageChangeDTO;
import org.example.policyservice.dto.PlanCountDTO;
import org.example.policyservice.dto.PolicyEnrollDTO;
import org.example.policyservice.dto.PolicyStatusCountDTO;
import org.example.policyservice.dto.PolicyUserDTO;
import org.example.policyservice.exception.PlanNotFoundException;
import org.example.policyservice.exception.PolicyNotEnrolledByAgentException;
import org.example.policyservice.exception.PolicyNotFoundException;
import org.example.policyservice.exception.UserAlreadyEnrolledException;
import org.example.policyservice.exception.UserNotEnrolledException;
import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.model.entity.Policy;
import org.example.policyservice.model.enums.Status;
import org.example.policyservice.repository.PlanRepository;
import org.example.policyservice.repository.PolicyUserRepository;
import org.example.policyservice.service.PolicyService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PolicyServiceImpl implements PolicyService {

    private final PlanRepository planRepository;
    private final PolicyUserRepository policyRepository;
    private final KafkaTemplate<String, Policy> kafkaTemplate;

    public PolicyServiceImpl(PlanRepository planRepository ,PolicyUserRepository policyRepository, KafkaTemplate<String, Policy> kafkaTemplate){
        this.planRepository = planRepository;
        this.policyRepository = policyRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Policy enrollUser(PolicyEnrollDTO request) {
        Plan plan = planRepository.findPlanById(request.planId());

        if(plan==null){
            log.error("Plan does not exists for {}",request.planId());
            throw new PlanNotFoundException("Plan does not exists");
        }

        //check if user is already reigstered under the policy
        if(policyRepository.existsPolicyUserByUserIdAndPlanAndStatus(request.userId(), plan, Status.ACTIVE)){
            throw new UserAlreadyEnrolledException("User already enrolled in plan");
        }

        Policy policy = new Policy(plan,request.userId(),LocalDate.now(),
                LocalDate.now().plusMonths(plan.getDuration()), Status.ACTIVE,plan.getCoverageAmount(), 0);

        policy.setAgentId(request.agentId());

        policyRepository.save(policy);
        kafkaTemplate.send("policy-purchase-email", policy);
        return policy;
    }

    @Override
    public void cancelPolicy(PolicyUserDTO request) {
        Policy policy = policyRepository.findById(request.policyId())
                .orElseThrow(() -> new PolicyNotFoundException("Policy does not exist: " + request.policyId()));

        if(!Objects.equals(request.userId(), policy.getUserId())){
            throw new UserNotEnrolledException("User does not match user");
        }

        if(request.agentId()!=null && !Objects.equals(request.agentId(), policy.getAgentId())){
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

        if(policy.getStatus()==Status.CANCELLED || policy.getStatus()==Status.EXPIRED){
            log.info("user not enrolled in policy");
            throw new UserNotEnrolledException("User not enrolled in policy");
        }

        if(request.agentId()!=null && !Objects.equals(request.agentId(), policy.getAgentId())){
            log.error("Policy does not match agent id {}", policy.getAgentId());
            throw new PolicyNotEnrolledByAgentException("Policy was not enrolled by agent");
        }

        policy.setRenewalCounter(policy.getRenewalCounter()+1);
        policy.setEndDate(policy.getEndDate().plusMonths(policy.getPlan().getDuration()));


        policyRepository.save(policy);
        kafkaTemplate.send("policy-purchase-email", policy);
        return policy;
    }

    @Override
    public List<Policy> viewAllRegisteredPolicies(String userId) {
        return policyRepository.findByUserId(userId);
    }

    @Override
    public Policy changeCoverage(CoverageChangeDTO request) {
        Policy policy = policyRepository.findById(request.policyId())
                .orElseThrow(()->new PolicyNotFoundException("User not enrolled in policy"));

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

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendRenewalReminder(){
        LocalDate reminderDate = LocalDate.now().plusDays(7);
        List<Policy> policies = policyRepository.findByEndDateAndStatus(reminderDate, Status.ACTIVE);
        policies.stream().forEach(policy -> {
            log.info(policy.toString());
            kafkaTemplate.send("policy-renewal-reminder",policy);
        });

    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void expirePolicies() {
        List<Policy> expiredPolicies = policyRepository.findByEndDateBeforeAndStatus( LocalDate.now(), Status.ACTIVE);

        expiredPolicies.forEach(policy -> {
            policy.setStatus(Status.EXPIRED);
            policyRepository.save(policy);
        });
    }


    @Override
    public Policy getEnrollment(String userId, Long policyId) {
        Plan plan = planRepository.findPlanById(policyId);
        return  policyRepository.findByUserIdAndPlanAndStatus(userId, plan, Status.ACTIVE);
    }

    @Override
    public List<PlanCountDTO> getMostPurchasedPlansLastMonth() {
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);

        List<Object[]> results = policyRepository.findPlanCounts(oneMonthAgo, today);
        List<PlanCountDTO> reportList = new ArrayList<>();

        results.stream().forEach(row->{
            Plan plan = (Plan) row[0];
            Long count = (Long) row[1];
            reportList.add(new PlanCountDTO(plan.getName(), count.intValue()));
        });

        return reportList;
    }

    @Override
    public List<PolicyStatusCountDTO> getPolicyCountByStatus() {
        List<Object[]> results = policyRepository.countPoliciesByStatus();
        List<PolicyStatusCountDTO> response = new ArrayList<>();

        results.stream().forEach(row->{
            Status status = (Status) row[0];
            Long count = (Long) row[1];
            response.add(new PolicyStatusCountDTO(status, count));
        });

        return response;
    }
}
