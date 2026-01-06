package org.example.emailservice.kafka;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.emailservice.dto.ClaimDTO;
import org.example.emailservice.dto.ClaimStatus;
import org.example.emailservice.dto.OtpDTO;
import org.example.emailservice.dto.PayoutDTO;
import org.example.emailservice.dto.PlanDTO;
import org.example.emailservice.dto.PolicyDTO;
import org.example.emailservice.dto.UserDTO;
import org.example.emailservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KafkaControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private KafkaController kafkaController;

    @Test
    void handlePolicyPurchase_shouldCallEmailService() {
        PlanDTO plan = new PlanDTO("Silver", "Basic", 100.0, 1000.0, 12);
        PolicyDTO request = new PolicyDTO(plan, "user1", LocalDate.now(), LocalDate.now().plusYears(1), 0);

        kafkaController.handlePolicyPurchase(request);

        verify(emailService).sendConformationPolicyPurchase(request);
    }

    @Test
    void handleOtp_shouldCallEmailService() {
        OtpDTO request = new OtpDTO("test@example.com", "123456");

        kafkaController.handleOtp(request);

        verify(emailService).sendOtp(request);
    }

    @Test
    void handleAdminAccount_shouldCallEmailService() {
        UserDTO request = new UserDTO("Admin", "admin1", "admin@test.com", "pass");

        kafkaController.handleAdminAccount(request);

        verify(emailService).sendAdminAccountCreationDetails(request);
    }

    @Test
    void handleClaimSubmission_shouldCallEmailService() {
        ClaimDTO request = new ClaimDTO(1L, "user1", 500.0, LocalDateTime.now(), 10L, ClaimStatus.SUBMITTED);

        kafkaController.handleClaimSubmission(request);

        verify(emailService).sendClaimSubmissionDetails(request);
    }

    @Test
    void handleClaimDecision_shouldCallEmailService() {
        ClaimDTO request = new ClaimDTO(1L, "user1", 500.0, LocalDateTime.now(), 10L, ClaimStatus.APPROVED);

        kafkaController.handleClaimDecision(request);

        verify(emailService).sendClaimApprovalOrRejection(request);
    }

    @Test
    void handleRenewalReminder_shouldCallEmailService() {
        PlanDTO plan = new PlanDTO("Gold", "Premium", 200.0, 5000.0, 12);
        PolicyDTO request = new PolicyDTO(plan, "user1", LocalDate.now(), LocalDate.now().plusYears(1), 0);

        kafkaController.handleRenewalReminder(request);

        verify(emailService).sendRenewalReminder(request);
    }

    @Test
    void handlePayout_shouldCallEmailService() {
        PayoutDTO request = new PayoutDTO("user1", 10L, 500.0);

        kafkaController.handlePayout(request);

        verify(emailService).sendPayoutDetails(request);
    }
}