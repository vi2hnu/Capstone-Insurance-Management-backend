package org.example.claimsservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.example.claimsservice.dto.AddClaimsDTO;
import org.example.claimsservice.dto.ClaimsOfficerValidationDTO;
import org.example.claimsservice.dto.PlanDTO;
import org.example.claimsservice.dto.PolicyDTO;
import org.example.claimsservice.dto.ProviderVerificationDTO;
import org.example.claimsservice.exception.ClaimNotFoundException;
import org.example.claimsservice.exception.InvalidPolicyClaimException;
import org.example.claimsservice.exception.InvalidStageException;
import org.example.claimsservice.exception.PolicyNotFoundException;
import org.example.claimsservice.exception.UnauthorizedClaimReviewException;
import org.example.claimsservice.feign.PolicyService;
import org.example.claimsservice.feign.ProviderService;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.model.entity.ClaimReview;
import org.example.claimsservice.model.enums.ClaimStage;
import org.example.claimsservice.model.enums.ClaimStatus;
import org.example.claimsservice.model.enums.ClaimSubmissionEntity;
import org.example.claimsservice.model.enums.ReviewStatus;
import org.example.claimsservice.repository.ClaimRepository;
import org.example.claimsservice.repository.ClaimReviewRepository;
import org.example.claimsservice.service.implementation.ClaimServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import feign.FeignException;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private PolicyService policyService;
    @Mock
    private ProviderService providerService;
    @Mock
    private ClaimReviewRepository claimReviewRepository;
    @Mock
    private KafkaTemplate<String, Claim> kafkaTemplate;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private PolicyDTO createPolicy(String userId, String status, Double remainingCoverage, String agentId) {
        PlanDTO plan = new PlanDTO("Gold Plan", "Desc", 500.0, 50000.0, 12, "ACTIVE", 10L);
        return new PolicyDTO(
            plan,
            userId,
            LocalDate.now().minusMonths(1),
            LocalDate.now().plusMonths(11),
            status,
            remainingCoverage,
            0,
            agentId,
            100L
        );
    }

    @Test
    void addClaim_savesClaimAndSendsKafka_whenValid() {
        AddClaimsDTO request = new AddClaimsDTO(100L, "user1", 1L, 5000.0, "doc.pdf", null);
        PolicyDTO policy = createPolicy("user1", "ACTIVE", 10000.0, null);

        when(policyService.getPolicyById(100L)).thenReturn(policy);
        when(providerService.checkHospitalPlan(10L, 1L)).thenReturn(true);
        when(claimRepository.save(any(Claim.class))).thenAnswer(i -> i.getArgument(0));

        Claim result = claimService.addClaim(request);

        assertNotNull(result);
        assertEquals(ClaimStage.PROVIDER, result.getStage());
        assertEquals(ClaimStatus.SUBMITTED, result.getStatus());
        verify(kafkaTemplate).send(eq("claim-submission-email"), any(Claim.class));
    }

    @Test
    void addClaim_throwsPolicyNotFound_whenFeignFails() {
        AddClaimsDTO request = new AddClaimsDTO(100L, "user1", 1L, 5000.0, "doc.pdf", null);
        when(policyService.getPolicyById(100L)).thenThrow(FeignException.NotFound.class);

        assertThrows(PolicyNotFoundException.class, () -> claimService.addClaim(request));
    }

    @Test
    void addClaim_throwsPolicyNotFound_whenPolicyInactive() {
        AddClaimsDTO request = new AddClaimsDTO(100L, "user1", 1L, 5000.0, "doc.pdf", null);
        PolicyDTO policy = createPolicy("user1", "INACTIVE", 10000.0, null);

        when(policyService.getPolicyById(100L)).thenReturn(policy);

        assertThrows(PolicyNotFoundException.class, () -> claimService.addClaim(request));
    }

    @Test
    void addClaim_throwsInvalidPolicyClaim_whenAgentMismatch() {
        AddClaimsDTO request = new AddClaimsDTO(100L, "user1", 1L, 5000.0, "doc.pdf", "AgentA");
        PolicyDTO policy = createPolicy("user1", "ACTIVE", 10000.0, "AgentB");

        when(policyService.getPolicyById(100L)).thenReturn(policy);

        assertThrows(InvalidPolicyClaimException.class, () -> claimService.addClaim(request));
    }

    @Test
    void addClaim_throwsInvalidPolicyClaim_whenUserMismatch() {
        AddClaimsDTO request = new AddClaimsDTO(100L, "user2", 1L, 5000.0, "doc.pdf", null);
        PolicyDTO policy = createPolicy("user1", "ACTIVE", 10000.0, null);

        when(policyService.getPolicyById(100L)).thenReturn(policy);

        assertThrows(InvalidPolicyClaimException.class, () -> claimService.addClaim(request));
    }

    @Test
    void addClaim_throwsInvalidPolicyClaim_whenInsufficientCoverage() {
        AddClaimsDTO request = new AddClaimsDTO(100L, "user1", 1L, 20000.0, "doc.pdf", null);
        PolicyDTO policy = createPolicy("user1", "ACTIVE", 10000.0, null);

        when(policyService.getPolicyById(100L)).thenReturn(policy);

        assertThrows(InvalidPolicyClaimException.class, () -> claimService.addClaim(request));
    }

    @Test
    void addClaim_throwsInvalidPolicyClaim_whenHospitalPlanInvalid() {
        AddClaimsDTO request = new AddClaimsDTO(100L, "user1", 1L, 5000.0, "doc.pdf", null);
        PolicyDTO policy = createPolicy("user1", "ACTIVE", 10000.0, null);

        when(policyService.getPolicyById(100L)).thenReturn(policy);
        when(providerService.checkHospitalPlan(10L, 1L)).thenReturn(false);

        assertThrows(InvalidPolicyClaimException.class, () -> claimService.addClaim(request));
    }

    @Test
    void providerAddClaim_savesClaimWithOfficerStage_whenValid() {
        AddClaimsDTO request = new AddClaimsDTO(100L, "user1", 1L, 5000.0, "doc.pdf", null);
        PolicyDTO policy = createPolicy("user1", "ACTIVE", 10000.0, null);

        when(policyService.getPolicyById(100L)).thenReturn(policy);
        when(providerService.checkHospitalPlan(10L, 1L)).thenReturn(true);
        when(claimRepository.save(any(Claim.class))).thenAnswer(i -> i.getArgument(0));

        Claim result = claimService.providerAddClaim(request);

        assertEquals(ClaimStage.CLAIMS_OFFICER, result.getStage());
        assertEquals(ClaimSubmissionEntity.PROVIDER, result.getSubmittedBy());
    }

    @Test
    void providerVerification_advancesStage_whenAuthorizedAndValid() {
        ProviderVerificationDTO request = new ProviderVerificationDTO(50L, "1", ReviewStatus.APPROVED, "Ok");

        Claim claim = new Claim();
        claim.setId(50L);
        claim.setHospitalId(1L);
        claim.setStage(ClaimStage.PROVIDER);

        when(claimRepository.findById(50L)).thenReturn(Optional.of(claim));
        when(providerService.checkAssociation(any(), eq(1L))).thenReturn(true);
        when(claimRepository.save(any(Claim.class))).thenAnswer(i -> i.getArgument(0));

        Claim result = claimService.providerVerification(request);

        assertEquals(ClaimStage.CLAIMS_OFFICER, result.getStage());
        assertEquals(ClaimStatus.IN_REVIEW, result.getStatus());
        verify(claimReviewRepository).save(any(ClaimReview.class));
    }

    @Test
    void providerVerification_throwsClaimNotFound_whenIdInvalid() {
        ProviderVerificationDTO request = new ProviderVerificationDTO(999L, "1", ReviewStatus.APPROVED, "Ok");
        when(claimRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ClaimNotFoundException.class, () -> claimService.providerVerification(request));
    }

    @Test
    void providerVerification_throwsUnauthorized_whenNotAssociated() {
        ProviderVerificationDTO request = new ProviderVerificationDTO(50L, "2", ReviewStatus.APPROVED, "Ok");
        Claim claim = new Claim();
        claim.setId(50L);
        claim.setHospitalId(1L);

        when(claimRepository.findById(50L)).thenReturn(Optional.of(claim));
        when(providerService.checkAssociation(any(), eq(1L))).thenThrow(FeignException.BadRequest.class);

        assertThrows(UnauthorizedClaimReviewException.class, () -> claimService.providerVerification(request));
    }

    @Test
    void providerVerification_throwsInvalidStage_whenNotProviderStage() {
        ProviderVerificationDTO request = new ProviderVerificationDTO(50L, "1", ReviewStatus.APPROVED, "Ok");
        Claim claim = new Claim();
        claim.setId(50L);
        claim.setHospitalId(1L);
        claim.setStage(ClaimStage.CLAIMS_OFFICER);

        when(claimRepository.findById(50L)).thenReturn(Optional.of(claim));
        when(providerService.checkAssociation(any(), eq(1L))).thenReturn(true);

        assertThrows(InvalidStageException.class, () -> claimService.providerVerification(request));
    }

    @Test
    void claimsOfficerValidation_approvesAndSendsKafka_whenApproved() {
        ClaimsOfficerValidationDTO request =
            new ClaimsOfficerValidationDTO(50L, "officer1", ReviewStatus.APPROVED, "Looks good");
        Claim claim = new Claim();
        claim.setId(50L);
        claim.setStage(ClaimStage.CLAIMS_OFFICER);

        when(claimRepository.findById(50L)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(i -> i.getArgument(0));

        Claim result = claimService.claimsOfficerValidation(request);

        assertEquals(ClaimStatus.APPROVED, result.getStatus());
        assertEquals(ClaimStage.PAYMENT, result.getStage());
        verify(kafkaTemplate).send(eq("claim-payout"), any(Claim.class));
        verify(kafkaTemplate, times(2)).send(anyString(), any(Claim.class));
    }

    @Test
    void claimsOfficerValidation_rejectsAndCompletes_whenRejected() {
        ClaimsOfficerValidationDTO request =
            new ClaimsOfficerValidationDTO(50L, "officer1", ReviewStatus.REJECTED, "Bad");
        Claim claim = new Claim();
        claim.setId(50L);
        claim.setStage(ClaimStage.CLAIMS_OFFICER);

        when(claimRepository.findById(50L)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(i -> i.getArgument(0));

        Claim result = claimService.claimsOfficerValidation(request);

        assertEquals(ClaimStatus.REJECTED, result.getStatus());
        assertEquals(ClaimStage.COMPLETED, result.getStage());
        verify(kafkaTemplate).send(eq("claim-decision-email"), any(Claim.class));
        verify(kafkaTemplate, never()).send(eq("claim-payout"), any(Claim.class));
    }

    @Test
    void claimsOfficerValidation_throwsInvalidStage_whenNotOfficerStage() {
        ClaimsOfficerValidationDTO request =
            new ClaimsOfficerValidationDTO(50L, "officer1", ReviewStatus.APPROVED, "Ok");
        Claim claim = new Claim();
        claim.setId(50L);
        claim.setStage(ClaimStage.PAYMENT);

        when(claimRepository.findById(50L)).thenReturn(Optional.of(claim));

        assertThrows(InvalidStageException.class, () -> claimService.claimsOfficerValidation(request));
    }

    @Test
    void changeStatus_updatesToPaidAndCompleted() {
        Long claimId = 50L;
        Claim claim = new Claim();
        claim.setId(claimId);

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(i -> i.getArgument(0));

        Claim result = claimService.changeStatus(claimId);

        assertEquals(ClaimStatus.PAID, result.getStatus());
        assertEquals(ClaimStage.COMPLETED, result.getStage());
    }

    @Test
    void changeStatus_throwsNotFound_whenIdInvalid() {
        when(claimRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ClaimNotFoundException.class, () -> claimService.changeStatus(99L));
    }

    @Test
    void getClaimsByUserId_returnsList() {
        when(claimRepository.findByUserId("u1")).thenReturn(List.of(new Claim()));
        assertEquals(1, claimService.getClaimsByUserId("u1").size());
    }

    @Test
    void getClaimById_returnsClaim() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(new Claim()));
        assertNotNull(claimService.getClaimById(1L));
    }



    @Test
    void getSubmittedClaimsOfProvider_returnsClaims() {
        when(claimRepository.findByHospitalIdAndSubmittedBy(10L, ClaimSubmissionEntity.PROVIDER))
            .thenReturn(List.of(new Claim()));
        assertEquals(1, claimService.getSubmittedClaimsOfProvider(10L).size());
    }
}
