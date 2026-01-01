package org.example.emailservice.service.implementation;

import org.example.emailservice.dto.ClaimDTO;
import org.example.emailservice.dto.OtpDTO;
import org.example.emailservice.dto.PayoutDTO;
import org.example.emailservice.dto.PolicyDTO;
import org.example.emailservice.dto.UserDTO;
import org.example.emailservice.feign.IdentityService;
import org.example.emailservice.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender javaMailSender;
    private final IdentityService identityService;

    public EmailServiceImpl(JavaMailSender javaMailSender, IdentityService identityService) {
        this.javaMailSender = javaMailSender;
        this.identityService = identityService;
    }

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendConformationPolicyPurchase(PolicyDTO request) {
        UserDTO user = identityService.getUserById(request.userId());
        String message = "Hi " + user.name() + ",\n\n";

        if (request.renewalCount() > 0) {
            message += "Thank you for renewing your policy.\n";
        } else {
            message += "Thank you for purchasing a policy.\n";
        }

        message
                += "Plan: " + request.plan().name() + "\n"
                + "Valid: " + request.startDate() + " to " + request.endDate() + "\n\n"
                + "Regards,\nInsurance Team";

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(sender);
        mail.setTo(user.email());
        mail.setSubject("Policy Purchase Confirmation");
        mail.setText(message);

        javaMailSender.send(mail);
    }

    @Override
    public void sendOtp(OtpDTO request) {
        String message = "Greetings, Your OTP is: " + request.otp() + "\n\n";

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(sender);
        mail.setTo(request.email());
        mail.setSubject("Your OTP Code");
        mail.setText(message);

        javaMailSender.send(mail);
    }

    @Override
    public void sendAdminAccountCreationDetails(UserDTO request) {
        String message = "Greetings, " + request.name() + " your account has been created by admin.\n\n"
                + "Username: " + request.username() + "\n"
                + "Password: " + request.password() + "\n\n"
                + "Please change your password after your first login.\n\n";

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(sender);
        mail.setTo(request.email());
        mail.setSubject("Admin Account Created");
        mail.setText(message);

        javaMailSender.send(mail);
    }

    @Override
    public void sendClaimSubmissionDetails(ClaimDTO request) {
        UserDTO user = identityService.getUserById(request.userId());
        String message = "Greetings " + user.name() + ",\n\n"
                + "Your claim request has been submitted successfully.\n"
                + "Policy ID: " + request.policyId() + "\n"
                + "Requested Amount: RS." + request.requestedAmount() + "\n"
                + "Claim Request Date: " + request.claimRequestDate() + "\n\n"
                + "Track your claim status in your account." + request.id() + "\n\n"
                + "Regards,\nInsurance Team";

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(sender);
        mail.setTo(user.email());
        mail.setSubject("Claim Submission Confirmation");
        mail.setText(message);

        javaMailSender.send(mail);
    }

    @Override
    public void sendClaimApprovalOrRejection(ClaimDTO request) {
        UserDTO user = identityService.getUserById(request.userId());
        String message = "Greetings " + user.name() + ",\n\n"
                + "Your claim with ID: " + request.id() + " has been " + request.status() + ".\n\n"
                + "Check your account for more details.\n\n";

        if (request.status().toString().equals("APPROVED")) {
            message += "The approved amount of RS." + request.requestedAmount() + " will be processed shortly.\n\n";
        }

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(sender);
        mail.setTo(user.email());
        mail.setSubject("Claim Status Update");
        mail.setText(message);

        javaMailSender.send(mail);
    }

    @Override
    public void sendRenewalReminder(PolicyDTO request) {
        UserDTO user = identityService.getUserById(request.userId());
        String message = "Greetings " + user.name() + ",\n\n"
                + "Your policy is about to expire.\n"
                + "Plan: " + request.plan().name() + "\n"
                + "Valid: " + request.startDate() + " to " + request.endDate() + "\n\n"
                + "Regards,\nInsurance Team";

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(sender);
        mail.setTo(user.email());
        mail.setSubject("Policy Renewal Reminder");
        mail.setText(message);

        javaMailSender.send(mail);
    }

    @Override
    public void sendPayoutDetails(PayoutDTO request) {
        UserDTO user = identityService.getUserById(request.userId());
        String message = "Greetings " + user.name() + ",\n\n"
                + "Your payout of RS." + request.amount() + " for Claim ID: " + request.claimId() + " has been processed.\n\n"
                + "Regards,\nInsurance Team";

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(sender);
        mail.setTo(user.email());
        mail.setSubject("Payout Processed");
        mail.setText(message);

        javaMailSender.send(mail);
    }

}
