package org.example.billingservice.service;

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
import org.example.billingservice.service.implementation.PayoutServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class PayoutServiceImplTest {

    @Mock
    private PolicyService policyService;

    @Mock
    private ProviderService providerService;

    @Mock
    private ProviderPayoutRepository providerPayoutRepository;

    @Mock
    private UserPayoutRepository userPayoutRepository;

    @Mock
    private ClaimService claimService;

    @Mock
    private KafkaTemplate<String, UserPayout> kafkaTemplate;

    @InjectMocks
    private PayoutServiceImpl payoutService;

    @Test
    void payout_whenProviderInNetwork_paysHospitalAndUpdatesExternalServices() {
        Long claimId = 100L;
        Long policyId = 200L;
        Long hospitalId = 300L;
        Long planId = 50L;
        Double amount = 5000.0;
        String userId = "user-123";

        PayoutDTO request = new PayoutDTO(claimId, userId, hospitalId, amount, policyId);
        PolicyPlanDTO policyPlanDTO = new PolicyPlanDTO(new PolicyPlanDTO.Plan(planId));

        when(policyService.getPolicy(policyId)).thenReturn(policyPlanDTO);
        when(providerService.getProviderType(planId, hospitalId)).thenReturn(ProviderType.IN_NETWORK);

        payoutService.payout(request);

        ArgumentCaptor<ProviderPayout> payoutCaptor = ArgumentCaptor.forClass(ProviderPayout.class);
        verify(providerPayoutRepository).save(payoutCaptor.capture());
        ProviderPayout savedPayout = payoutCaptor.getValue();

        assertEquals(hospitalId, savedPayout.getProviderId());
        assertEquals(claimId, savedPayout.getClaimId());
        assertEquals(amount, savedPayout.getAmount());

        verify(userPayoutRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any());
        verify(claimService).markAsPaid(claimId);

        ArgumentCaptor<CoverageChangeDTO> coverageCaptor = ArgumentCaptor.forClass(CoverageChangeDTO.class);
        verify(policyService).changeClaimedAmount(coverageCaptor.capture());
        assertEquals(policyId, coverageCaptor.getValue().policyId());
    }

    @Test
    void payout_whenProviderOutOfNetwork_paysUserAndSendsKafkaNotification() {
        Long claimId = 101L;
        Long policyId = 201L;
        Long hospitalId = 301L;
        Long planId = 51L;
        Double amount = 1500.0;
        String userId = "user-456";

        PayoutDTO request = new PayoutDTO(claimId, userId, hospitalId, amount, policyId);
        PolicyPlanDTO policyPlanDTO = new PolicyPlanDTO(new PolicyPlanDTO.Plan(planId));

        when(policyService.getPolicy(policyId)).thenReturn(policyPlanDTO);
        when(providerService.getProviderType(planId, hospitalId)).thenReturn(ProviderType.OUT_NETWORK);

        payoutService.payout(request);

        ArgumentCaptor<UserPayout> userPayoutCaptor = ArgumentCaptor.forClass(UserPayout.class);
        verify(userPayoutRepository).save(userPayoutCaptor.capture());
        UserPayout savedPayout = userPayoutCaptor.getValue();

        assertEquals(userId, savedPayout.getUserId());
        assertEquals(claimId, savedPayout.getClaimId());
        assertEquals(amount, savedPayout.getAmount());

        verify(kafkaTemplate).send(eq("payout-email"), eq(savedPayout));
        verify(providerPayoutRepository, never()).save(any());
        verify(claimService).markAsPaid(claimId);
        verify(policyService).changeClaimedAmount(any(CoverageChangeDTO.class));
    }

    @Test
    void policyFallback_shouldThrowServiceUnavailableException() {
        Long policyId = 999L;
        Throwable exception = new RuntimeException("Connection Refused");

        ServiceUnavailableException thrown = assertThrows(ServiceUnavailableException.class, () -> {
            // Using reflection or package-private access if in same package
            java.lang.reflect.Method method = PayoutServiceImpl.class.getDeclaredMethod("policyFallback", Long.class, Throwable.class);
            method.setAccessible(true);
            try {
                method.invoke(payoutService, policyId, exception);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        });

        assertEquals("Unable to process payout - policy service unavailable", thrown.getMessage());
    }

    @Test
    void providerFallback_shouldThrowServiceUnavailableException() {
        Long planId = 10L;
        Long hospitalId = 20L;
        Throwable exception = new RuntimeException("Timeout");

        ServiceUnavailableException thrown = assertThrows(ServiceUnavailableException.class, () -> {
            java.lang.reflect.Method method = PayoutServiceImpl.class.getDeclaredMethod("providerFallback", Long.class, Long.class, Throwable.class);
            method.setAccessible(true);
            try {
                method.invoke(payoutService, planId, hospitalId, exception);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        });

        assertEquals("Provider service unavailable", thrown.getMessage());
    }

    @Test
    void claimFallback_shouldThrowServiceUnavailableException() {
        Long claimId = 55L;
        Throwable exception = new RuntimeException("Network Error");

        ServiceUnavailableException thrown = assertThrows(ServiceUnavailableException.class, () -> {
            java.lang.reflect.Method method = PayoutServiceImpl.class.getDeclaredMethod("claimFallback", Long.class, Throwable.class);
            method.setAccessible(true);
            try {
                method.invoke(payoutService, claimId, exception);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        });

        assertEquals("Claim service unavailable", thrown.getMessage());
    }
}