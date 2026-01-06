package org.example.policyservice.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.example.policyservice.service.implementation.PolicyServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PolicyUserRepository policyRepository;

    @Mock
    private KafkaTemplate<String, Policy> kafkaTemplate;

    @InjectMocks
    private PolicyServiceImpl policyService;

    @Test
    void enrollUser_shouldEnrollSuccessfully() {
        Long planId = 1L;
        String userId = "user1";
        Plan plan = new Plan("Gold", "Desc", 100.0, 5000.0, 12, Status.ACTIVE);
        plan.setId(planId);
        PolicyEnrollDTO request = new PolicyEnrollDTO(userId, planId, "agent1");

        when(planRepository.findPlanById(planId)).thenReturn(plan);
        when(policyRepository.existsPolicyUserByUserIdAndPlanAndStatus(userId, plan, Status.ACTIVE)).thenReturn(false);

        Policy result = policyService.enrollUser(request);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(plan, result.getPlan());
        assertEquals("agent1", result.getAgentId());
        assertEquals(Status.ACTIVE, result.getStatus());

        verify(policyRepository).save(any(Policy.class));
        verify(kafkaTemplate).send(eq("policy-purchase-email"), any(Policy.class));
    }

    @Test
    void enrollUser_shouldThrowPlanNotFoundException() {
        PolicyEnrollDTO request = new PolicyEnrollDTO("user1", 99L, null);
        when(planRepository.findPlanById(99L)).thenReturn(null);

        assertThrows(PlanNotFoundException.class, () -> policyService.enrollUser(request));
    }

    @Test
    void enrollUser_shouldThrowUserAlreadyEnrolledException() {
        Long planId = 1L;
        String userId = "user1";
        Plan plan = new Plan();
        PolicyEnrollDTO request = new PolicyEnrollDTO(userId, planId, null);

        when(planRepository.findPlanById(planId)).thenReturn(plan);
        when(policyRepository.existsPolicyUserByUserIdAndPlanAndStatus(userId, plan, Status.ACTIVE)).thenReturn(true);

        assertThrows(UserAlreadyEnrolledException.class, () -> policyService.enrollUser(request));
    }

    @Test
    void cancelPolicy_shouldCancelSuccessfully() {
        Long policyId = 100L;
        String userId = "user1";
        Policy policy = new Policy();
        policy.setId(policyId);
        policy.setUserId(userId);
        policy.setStatus(Status.ACTIVE);

        PolicyUserDTO request = new PolicyUserDTO(userId, policyId, null);

        when(policyRepository.findById(policyId)).thenReturn(Optional.of(policy));

        policyService.cancelPolicy(request);

        assertEquals(Status.CANCELLED, policy.getStatus());
        verify(policyRepository).save(policy);
    }

    @Test
    void cancelPolicy_shouldThrowUserNotEnrolledException_WhenUserMismatch() {
        Long policyId = 100L;
        Policy policy = new Policy();
        policy.setUserId("user1");

        PolicyUserDTO request = new PolicyUserDTO("user2", policyId, null);
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(policy));

        assertThrows(UserNotEnrolledException.class, () -> policyService.cancelPolicy(request));
    }

    @Test
    void cancelPolicy_shouldThrowPolicyNotEnrolledByAgentException() {
        Long policyId = 100L;
        String userId = "user1";
        Policy policy = new Policy();
        policy.setUserId(userId);
        policy.setAgentId("agent1");

        PolicyUserDTO request = new PolicyUserDTO(userId, policyId, "agent2");
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(policy));

        assertThrows(PolicyNotEnrolledByAgentException.class, () -> policyService.cancelPolicy(request));
    }

    @Test
    void renewPolicy_shouldRenewSuccessfully() {
        Long policyId = 100L;
        String userId = "user1";
        Plan plan = new Plan();
        plan.setDuration(12);

        Policy policy = new Policy();
        policy.setId(policyId);
        policy.setUserId(userId);
        policy.setPlan(plan);
        policy.setStatus(Status.ACTIVE);
        policy.setRenewalCounter(0);
        policy.setEndDate(LocalDate.now());

        PolicyUserDTO request = new PolicyUserDTO(userId, policyId, null);
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(policy));

        Policy result = policyService.renewPolicy(request);

        assertEquals(1, result.getRenewalCounter());
        verify(policyRepository).save(policy);
        verify(kafkaTemplate).send(eq("policy-purchase-email"), eq(policy));
    }

    @Test
    void renewPolicy_shouldThrowUserNotEnrolled_WhenCancelled() {
        Policy policy = new Policy();
        policy.setUserId("user1");
        policy.setStatus(Status.CANCELLED);

        PolicyUserDTO request = new PolicyUserDTO("user1", 1L, null);
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        assertThrows(UserNotEnrolledException.class, () -> policyService.renewPolicy(request));
    }

    @Test
    void viewAllRegisteredPolicies_shouldReturnList() {
        String userId = "user1";
        List<Policy> list = List.of(new Policy());
        when(policyRepository.findByUserId(userId)).thenReturn(list);

        List<Policy> result = policyService.viewAllRegisteredPolicies(userId);
        assertEquals(1, result.size());
    }

    @Test
    void changeCoverage_shouldUpdateCoverage() {
        Long policyId = 1L;
        Double initialCoverage = 5000.0;
        Double claimAmount = 1000.0;

        Policy policy = new Policy();
        policy.setStatus(Status.ACTIVE);
        policy.setRemainingCoverage(initialCoverage);

        CoverageChangeDTO request = new CoverageChangeDTO(policyId, claimAmount);
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(i -> i.getArguments()[0]);

        Policy result = policyService.changeCoverage(request);

        assertEquals(4000.0, result.getRemainingCoverage());
    }

    @Test
    void changeCoverage_shouldThrowWhenNotActive() {
        Policy policy = new Policy();
        policy.setStatus(Status.EXPIRED);

        CoverageChangeDTO request = new CoverageChangeDTO(1L, 100.0);
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        assertThrows(UserNotEnrolledException.class, () -> policyService.changeCoverage(request));
    }

    @Test
    void getPolicyById_shouldReturnPolicy() {
        Policy policy = new Policy();
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        assertNotNull(policyService.getPolicyById(1L));
    }

    @Test
    void getPolicyById_shouldThrowWhenNotFound() {
        when(policyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PolicyNotFoundException.class, () -> policyService.getPolicyById(1L));
    }

    @Test
    void sendRenewalReminder_shouldSendKafkaMessages() {
        Policy policy = new Policy();
        when(policyRepository.findByEndDateAndStatus(any(LocalDate.class), eq(Status.ACTIVE)))
                .thenReturn(List.of(policy));

        policyService.sendRenewalReminder();

        verify(kafkaTemplate).send("policy-renewal-reminder", policy);
    }

    @Test
    void expirePolicies_shouldUpdateStatus() {
        Policy policy = new Policy();
        policy.setStatus(Status.ACTIVE);

        when(policyRepository.findByEndDateBeforeAndStatus(any(LocalDate.class), eq(Status.ACTIVE)))
                .thenReturn(List.of(policy));

        policyService.expirePolicies();

        assertEquals(Status.EXPIRED, policy.getStatus());
        verify(policyRepository).save(policy);
    }

    @Test
    void getEnrollment_shouldReturnPolicy() {
        String userId = "user1";
        Long fakePlanId = 10L; 
        Plan plan = new Plan();
        Policy policy = new Policy();

        when(planRepository.findPlanById(fakePlanId)).thenReturn(plan);
        when(policyRepository.findByUserIdAndPlanAndStatus(userId, plan, Status.ACTIVE)).thenReturn(policy);

        Policy result = policyService.getEnrollment(userId, fakePlanId);
        assertEquals(policy, result);
    }

    @Test
    void getMostPurchasedPlansLastMonth_shouldMapToDTO() {
        Plan plan = new Plan();
        plan.setName("Best Plan");
        Object[] row = new Object[]{plan, 15L};
        List<Object[]> queryResult = new ArrayList<>();
        queryResult.add(row);

        when(policyRepository.findPlanCounts(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(queryResult);

        List<PlanCountDTO> result = policyService.getMostPurchasedPlansLastMonth();

        assertEquals(1, result.size());
        assertEquals("Best Plan", result.get(0).planName());
        assertEquals(15, result.get(0).count());
    }

    @Test
    void getPolicyCountByStatus_shouldMapToDTO() {
        Object[] row = new Object[]{Status.ACTIVE, 50L};
        List<Object[]> queryResult = new ArrayList<>();
        queryResult.add(row);

        when(policyRepository.countPoliciesByStatus()).thenReturn(queryResult);

        List<PolicyStatusCountDTO> result = policyService.getPolicyCountByStatus();

        assertEquals(1, result.size());
        assertEquals(Status.ACTIVE, result.get(0).status());
        assertEquals(50, result.get(0).count());
    }
}