package org.example.billingservice.service.Implementation;

import org.example.billingservice.Repository.ProviderPayoutRepository;
import org.example.billingservice.Repository.UserPayoutRepository;
import org.example.billingservice.dto.CoverageChangeDTO;
import org.example.billingservice.dto.PayoutDTO;
import org.example.billingservice.dto.PolicyPlanDTO;
import org.example.billingservice.feign.ClaimService;
import org.example.billingservice.feign.PolicyService;
import org.example.billingservice.feign.ProviderService;
import org.example.billingservice.model.entity.ProviderPayout;
import org.example.billingservice.model.entity.UserPayout;
import org.example.billingservice.model.enums.ProviderType;
import org.example.billingservice.service.PayoutService;
import org.springframework.stereotype.Service;
;

@Service
public class PayoutServiceImpl implements PayoutService {

    private final PolicyService  policyService;
    private final ProviderService providerService;
    private final ProviderPayoutRepository  providerPayoutRepository;
    private final UserPayoutRepository  userPayoutRepository;
    private final ClaimService claimService;

    public PayoutServiceImpl(PolicyService policyService,ProviderService providerService,
                             ProviderPayoutRepository providerPayoutRepository, UserPayoutRepository userPayoutRepository,
                             ClaimService claimService) {
        this.policyService = policyService;
        this.providerService = providerService;
        this.providerPayoutRepository = providerPayoutRepository;
        this.userPayoutRepository = userPayoutRepository;
        this.claimService = claimService;
    }

    @Override
    public void payout(PayoutDTO request){
        PolicyPlanDTO planId = policyService.getPolicy(request.policyId());
        ProviderType providerType = providerService.getProviderType(planId.plan().id(), request.providerId());
        if(providerType==ProviderType.IN_NETWORK){
            payHospital(request);
        }
        else{
            payUser(request);
        }
        claimService.markAsPaid(request.claimId());
        policyService.changeClaimedAmount(new CoverageChangeDTO(request.policyId(),request.amount()));

    }

    private void payHospital(PayoutDTO request){
        providerPayoutRepository.save(new ProviderPayout(request.providerId(), request.claimId(), request.amount()));

    }

    private void payUser(PayoutDTO request){
        userPayoutRepository.save(new UserPayout(request.userId(), request.claimId(), request.amount()));
    }
}
