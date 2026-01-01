package org.example.emailservice.service;

import org.example.emailservice.dto.ClaimDTO;
import org.example.emailservice.dto.OtpDTO;
import org.example.emailservice.dto.PayoutDTO;
import org.example.emailservice.dto.PolicyDTO;
import org.example.emailservice.dto.UserDTO;

public interface EmailService {
    void sendConformationPolicyPurchase(PolicyDTO request);
    void sendOtp(OtpDTO request);
    void sendAdminAccountCreationDetails(UserDTO request);
    void sendClaimSubmissionDetails(ClaimDTO request);
    void sendClaimApprovalOrRejection(ClaimDTO request);
    void sendRenewalReminder(PolicyDTO request);
    void sendPayoutDetails(PayoutDTO request);
}
