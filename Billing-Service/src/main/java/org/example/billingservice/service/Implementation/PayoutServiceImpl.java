package org.example.billingservice.service.Implementation;

import org.example.billingservice.Repository.ProviderPayoutRepository;
import org.example.billingservice.Repository.UserPayoutRepository;
import org.example.billingservice.dto.PayoutDTO;
import org.example.billingservice.dto.PolicyPlanDTO;
import org.example.billingservice.feign.PolicyService;
import org.example.billingservice.feign.ProviderService;
import org.example.billingservice.model.entity.ProviderPayout;
import org.example.billingservice.model.entity.UserPayout;
import org.example.billingservice.service.PayoutService;
import org.springframework.stereotype.Service;

import java.security.Provider;
import java.util.Objects;

@Service
public class PayoutServiceImpl implements PayoutService {

    private final PolicyService  policyService;
    private final ProviderService providerService;
    private final ProviderPayoutRepository  providerPayoutRepository;
    private final UserPayoutRepository  userPayoutRepository;

    public PayoutServiceImpl(PolicyService policyService,ProviderService providerService,
                             ProviderPayoutRepository providerPayoutRepository, UserPayoutRepository userPayoutRepository) {
        this.policyService = policyService;
        this.providerService = providerService;
        this.providerPayoutRepository = providerPayoutRepository;
        this.userPayoutRepository = userPayoutRepository;
    }

    @Override
    public void payout(PayoutDTO request){
        PolicyPlanDTO planId = policyService.getPolicy(request.policyId());
        String providerType = providerService.getProviderType(planId.plan().id(), request.providerId());
        if(Objects.equals(providerType, "IN_NETWORK")){
            payHospital(request);
        }
        payUser(request);
    }

    private void payHospital(PayoutDTO request){
        providerPayoutRepository.save(new ProviderPayout(request.providerId(), request.claimId(), request.amount()));
    }

    private void payUser(PayoutDTO request){
        userPayoutRepository.save(new UserPayout(request.userId(), request.claimId(), request.amount()));
    }
}
