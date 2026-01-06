package org.example.emailservice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.emailservice.dto.ClaimDTO;
import org.example.emailservice.dto.ClaimStatus;
import org.example.emailservice.dto.OtpDTO;
import org.example.emailservice.dto.PayoutDTO;
import org.example.emailservice.dto.PlanDTO;
import org.example.emailservice.dto.PolicyDTO;
import org.example.emailservice.dto.UserDTO;
import org.example.emailservice.feign.IdentityService;
import org.example.emailservice.service.implementation.EmailServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private IdentityService identityService;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String SENDER_EMAIL = "test@insurance.com";

    @BeforeEach
    void setUp() {
        // Inject the @Value("${spring.mail.username}") field manually
        ReflectionTestUtils.setField(emailService, "sender", SENDER_EMAIL);
    }

    @Test
    void sendConformationPolicyPurchase_NewPurchase_SendsEmail() {
        // Arrange
        String userId = "user123";
        PlanDTO plan = new PlanDTO("Gold Plan", "Desc", 100.0, 5000.0, 12);
        PolicyDTO policyDTO = new PolicyDTO(plan, userId, LocalDate.now(), LocalDate.now().plusYears(1), 0);
        UserDTO userDTO = new UserDTO("John Doe", "john", "john@example.com", "pass");

        when(identityService.getUserById(userId)).thenReturn(userDTO);

        // Act
        emailService.sendConformationPolicyPurchase(policyDTO);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(SENDER_EMAIL, sentMessage.getFrom());
        assertEquals("john@example.com", sentMessage.getTo()[0]);
        assertEquals("Policy Purchase Confirmation", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("Thank you for purchasing a policy"));
        assertTrue(sentMessage.getText().contains("Gold Plan"));
    }

    @Test
    void sendConformationPolicyPurchase_Renewal_SendsEmail() {
        // Arrange
        String userId = "user123";
        PlanDTO plan = new PlanDTO("Gold Plan", "Desc", 100.0, 5000.0, 12);
        PolicyDTO policyDTO = new PolicyDTO(plan, userId, LocalDate.now(), LocalDate.now().plusYears(1), 1); // Renewal count > 0
        UserDTO userDTO = new UserDTO("John Doe", "john", "john@example.com", "pass");

        when(identityService.getUserById(userId)).thenReturn(userDTO);

        // Act
        emailService.sendConformationPolicyPurchase(policyDTO);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        
        assertTrue(messageCaptor.getValue().getText().contains("Thank you for renewing your policy"));
    }

    @Test
    void sendOtp_SendsEmail() {
        // Arrange
        OtpDTO otpDTO = new OtpDTO("test@example.com", "123456");

        // Act
        emailService.sendOtp(otpDTO);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertEquals("Your OTP Code", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("123456"));
    }

    @Test
    void sendAdminAccountCreationDetails_SendsEmail() {
        // Arrange
        UserDTO userDTO = new UserDTO("Admin User", "admin_usr", "admin@example.com", "securePass");

        // Act
        emailService.sendAdminAccountCreationDetails(userDTO);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals("admin@example.com", sentMessage.getTo()[0]);
        assertEquals("Admin Account Created", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("admin_usr"));
        assertTrue(sentMessage.getText().contains("securePass"));
    }

    @Test
    void sendClaimSubmissionDetails_SendsEmail() {
        // Arrange
        String userId = "user123";
        ClaimDTO claimDTO = new ClaimDTO(100L, userId, 5000.0, LocalDateTime.now(), 55L, ClaimStatus.SUBMITTED);
        UserDTO userDTO = new UserDTO("John Doe", "john", "john@example.com", "pass");

        when(identityService.getUserById(userId)).thenReturn(userDTO);

        // Act
        emailService.sendClaimSubmissionDetails(claimDTO);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals("Claim Submission Confirmation", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("Policy ID: 100"));
        assertTrue(sentMessage.getText().contains("RS.5000.0"));
    }

    @Test
    void sendClaimApprovalOrRejection_Approved_SendsEmailWithAmount() {
        // Arrange
        String userId = "user123";
        ClaimDTO claimDTO = new ClaimDTO(100L, userId, 5000.0, LocalDateTime.now(), 55L, ClaimStatus.APPROVED);
        UserDTO userDTO = new UserDTO("John Doe", "john", "john@example.com", "pass");

        when(identityService.getUserById(userId)).thenReturn(userDTO);

        // Act
        emailService.sendClaimApprovalOrRejection(claimDTO);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals("Claim Status Update", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("APPROVED"));
        assertTrue(sentMessage.getText().contains("approved amount of RS.5000.0"));
    }

    @Test
    void sendClaimApprovalOrRejection_Rejected_SendsEmailWithoutAmountMessage() {
        // Arrange
        String userId = "user123";
        ClaimDTO claimDTO = new ClaimDTO(100L, userId, 5000.0, LocalDateTime.now(), 55L, ClaimStatus.REJECTED);
        UserDTO userDTO = new UserDTO("John Doe", "john", "john@example.com", "pass");

        when(identityService.getUserById(userId)).thenReturn(userDTO);

        // Act
        emailService.sendClaimApprovalOrRejection(claimDTO);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertTrue(sentMessage.getText().contains("REJECTED"));
        assertFalse(sentMessage.getText().contains("approved amount"));
    }

    @Test
    void sendRenewalReminder_SendsEmail() {
        // Arrange
        String userId = "user123";
        PlanDTO plan = new PlanDTO("Gold Plan", "Desc", 100.0, 5000.0, 12);
        PolicyDTO policyDTO = new PolicyDTO(plan, userId, LocalDate.now(), LocalDate.now().plusYears(1), 0);
        UserDTO userDTO = new UserDTO("John Doe", "john", "john@example.com", "pass");

        when(identityService.getUserById(userId)).thenReturn(userDTO);

        // Act
        emailService.sendRenewalReminder(policyDTO);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals("Policy Renewal Reminder", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("about to expire"));
        assertTrue(sentMessage.getText().contains("Gold Plan"));
    }

    @Test
    void sendPayoutDetails_SendsEmail() {
        // Arrange
        String userId = "user123";
        PayoutDTO payoutDTO = new PayoutDTO(userId, 55L, 2500.0);
        UserDTO userDTO = new UserDTO("John Doe", "john", "john@example.com", "pass");

        when(identityService.getUserById(userId)).thenReturn(userDTO);

        // Act
        emailService.sendPayoutDetails(payoutDTO);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals("Payout Processed", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("RS.2500.0"));
        assertTrue(sentMessage.getText().contains("Claim ID: 55"));
    }
}