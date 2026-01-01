package org.example.emailservice.kafka;

import org.example.emailservice.dto.ClaimDTO;
import org.example.emailservice.dto.OtpDTO;
import org.example.emailservice.dto.PayoutDTO;
import org.example.emailservice.dto.PolicyDTO;
import org.example.emailservice.dto.UserDTO;
import org.example.emailservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaController {

    private final EmailService emailService;

    public KafkaController(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "policy-purchase-email")
    public void handlePolicyPurchase(PolicyDTO request) {
        emailService.sendConformationPolicyPurchase(request);
    }

    @KafkaListener(topics = "otp-email")
    public void handleOtp(OtpDTO request) {
        emailService.sendOtp(request);
    }

    @KafkaListener(topics = "account-activation-email")
    public void handleAdminAccount(UserDTO request) {
        emailService.sendAdminAccountCreationDetails(request);
    }

    @KafkaListener(topics = "claim-submission-email")
    public void handleClaimSubmission(ClaimDTO request) {
        emailService.sendClaimSubmissionDetails(request);
    }

    @KafkaListener(topics = "claim-decision-email")
    public void handleClaimDecision(ClaimDTO request) {
        emailService.sendClaimApprovalOrRejection(request);
    }

    @KafkaListener(topics = "policy-renewal-reminder")
    public void handleRenewalReminder(PolicyDTO request) {
        emailService.sendRenewalReminder(request);
    }

    @KafkaListener(topics = "payout-email")
    public void handlePayout(PayoutDTO request) {
        emailService.sendPayoutDetails(request);
    }
}
