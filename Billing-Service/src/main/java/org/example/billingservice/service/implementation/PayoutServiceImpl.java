package org.example.billingservice.service.implementation;

import org.example.billingservice.dto.CoverageChangeDTO;
import org.example.billingservice.dto.PayoutDTO;
import org.example.billingservice.dto.PolicyPlanDTO;
import org.example.billingservice.exception.ServiceUnavailableException;
import org.example.billingservice.feign.ClaimService;
import org.example.billingservice.feign.PolicyService;
import org.example.billingservice.feign.ProviderService;
import org.example.billingservice.model.entity.ProviderPayout;
import org.example.billingservice.model.entity.UserPayout;
import org.example.billingservice.model.enums.ProviderType;
import org.example.billingservice.repository.ProviderPayoutRepository;
import org.example.billingservice.repository.UserPayoutRepository;
import org.example.billingservice.service.PayoutService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PayoutServiceImpl implements PayoutService {

    private final PolicyService  policyService;
    private final ProviderService providerService;
    private final ProviderPayoutRepository  providerPayoutRepository;
    private final UserPayoutRepository  userPayoutRepository;
    private final ClaimService claimService;
    private final KafkaTemplate<String,UserPayout> kafkaTemplate;

    public PayoutServiceImpl(PolicyService policyService,ProviderService providerService,
                             ProviderPayoutRepository providerPayoutRepository, UserPayoutRepository userPayoutRepository,
                             ClaimService claimService, KafkaTemplate<String,UserPayout> kafkaTemplate) {
        this.policyService = policyService;
        this.providerService = providerService;
        this.providerPayoutRepository = providerPayoutRepository;
        this.userPayoutRepository = userPayoutRepository;
        this.claimService = claimService;
        this.kafkaTemplate = kafkaTemplate;
    }

   @Override
    public void payout(PayoutDTO request) {
        PolicyPlanDTO plan = getPolicy(request.policyId());
        ProviderType providerType = getProviderType(plan.plan().id(), request.hospitalId());

        if (providerType == ProviderType.IN_NETWORK) {
            payHospital(request);
        } 
        else {
            payUser(request);
        }

        markClaimAsPaid(request.id());
        changeCoverage(request.policyId(), request.requestedAmount());
    }

    @CircuitBreaker(name = "policy-service", fallbackMethod = "policyFallback")
    PolicyPlanDTO getPolicy(Long policyId) {
        return policyService.getPolicy(policyId);
    }

    @CircuitBreaker(name = "provider-service", fallbackMethod = "providerFallback")
    ProviderType getProviderType(Long planId, Long hospitalId) {
        return providerService.getProviderType(planId, hospitalId);
    }

    @CircuitBreaker(name = "claim-service", fallbackMethod = "claimFallback")
    void markClaimAsPaid(Long claimId) {
        claimService.markAsPaid(claimId);
    }

    @CircuitBreaker(name = "policy-service", fallbackMethod = "policyFallback")
    void changeCoverage(Long policyId, Double amount) {
        policyService.changeClaimedAmount(new CoverageChangeDTO(policyId, amount));
    }

    PolicyPlanDTO policyFallback(Long policyId, Throwable ex) {
        log.error("Policy service unavailable for policyId: {}", policyId, ex);
        throw new ServiceUnavailableException("Unable to process payout - policy service unavailable");
    }

    ProviderType providerFallback(Long planId, Long hospitalId, Throwable ex) {
        throw new ServiceUnavailableException("Provider service unavailable");
    }

    void claimFallback(Long claimId, Throwable ex) {
        throw new ServiceUnavailableException("Claim service unavailable");
    }

    private void payHospital(PayoutDTO request){
        providerPayoutRepository.save(new ProviderPayout(request.hospitalId(), request.id(), request.requestedAmount()));
    }

    private void payUser(PayoutDTO request){
        UserPayout payout =new UserPayout(request.userId(), request.id(), request.requestedAmount());
        userPayoutRepository.save(payout);
        kafkaTemplate.send("payout-email",payout);
    }
}
