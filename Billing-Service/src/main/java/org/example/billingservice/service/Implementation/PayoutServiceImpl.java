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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
;

@Service
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
    public void payout(PayoutDTO request){
        PolicyPlanDTO planId = policyService.getPolicy(request.policyId());
        ProviderType providerType = providerService.getProviderType(planId.plan().id(), request.hospitalId());
        if(providerType==ProviderType.IN_NETWORK){
            payHospital(request);
        }
        else{
            payUser(request);
        }
        claimService.markAsPaid(request.id());
        policyService.changeClaimedAmount(new CoverageChangeDTO(request.policyId(),request.requestedAmount()));

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
