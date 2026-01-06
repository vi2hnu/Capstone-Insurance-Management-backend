package org.example.identityservice.controller;

import org.example.identityservice.dto.ChangePasswordDTO;
import org.example.identityservice.dto.ForgotPasswordDTO;
import org.example.identityservice.dto.MessageResponse;
import org.example.identityservice.dto.SignupDTO;
import org.example.identityservice.dto.ValidateOtpDTO;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.model.enums.Gender;
import org.example.identityservice.service.auth.AuthService;
import org.example.identityservice.service.jwt.JwtUtils;
import org.example.identityservice.service.otp.OtpService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerUser_Success_ShouldReturnOk() {
        SignupDTO signupDTO =
                new SignupDTO("Test Name", "testuser", "test@test.com", "password123", Gender.MALE);

        when(authService.signUp(signupDTO)).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        assertEquals(
                "User registered successfully!",
                ((MessageResponse) response.getBody()).getMessage()
        );
    }

    @Test
    void registerUser_Failure_ShouldReturnBadRequest() {
        SignupDTO signupDTO =
                new SignupDTO("Test Name", "testuser", "test@test.com", "password123", Gender.MALE);

        when(authService.signUp(signupDTO)).thenReturn(false);

        ResponseEntity<?> response = authController.registerUser(signupDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                "Error: account already exist",
                ((MessageResponse) response.getBody()).getMessage()
        );
    }

    @Test
    void logoutUser_ShouldReturnResponseWithCookieHeader() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "").build();
        when(jwtUtils.getCleanJwtCookie()).thenReturn(cookie);

        ResponseEntity<?> response = authController.logoutUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cookie.toString(), response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
        assertEquals(
                "You've been signed out!",
                ((MessageResponse) response.getBody()).getMessage()
        );
    }

    @Test
    void changePassword_ShouldCallServiceAndReturnOk() {
        ChangePasswordDTO request = new ChangePasswordDTO("testuser", "oldPass", "newPass");

        ResponseEntity<MessageResponse> response = authController.changePassword(request);

        verify(authService, times(1)).changePassword(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "User password has been changed. Please login again",
                response.getBody().getMessage()
        );
    }

    @Test
    void forgotPassword_ShouldReturnUser() {
        ForgotPasswordDTO request = new ForgotPasswordDTO("test@test.com", "newPass123");

        Users mockUser = new Users();
        mockUser.setEmail("test@test.com");

        when(authService.forgotPassword(request)).thenReturn(mockUser);

        ResponseEntity<Users> response = authController.forgotPassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    void generateOtp_ShouldReturnOtpString() {
        String email = "test@example.com";
        String otp = "123456";

        when(otpService.generateOtp(email)).thenReturn(otp);

        ResponseEntity<String> response = authController.generateOtp(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(otp, response.getBody());
    }

    @Test
    void validateOtp_ValidOtp_ShouldReturnTrue() {
        ValidateOtpDTO request = new ValidateOtpDTO("test@test.com", "123456");
        when(otpService.verifyOtp(request)).thenReturn(true);

        ResponseEntity<Boolean> response = authController.validateOtp(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
    }

    @Test
    void validateOtp_InvalidOtp_ShouldReturnBadRequestAndFalse() {
        ValidateOtpDTO request = new ValidateOtpDTO("test@test.com", "wrong-otp");
        when(otpService.verifyOtp(request)).thenReturn(false);

        ResponseEntity<Boolean> response = authController.validateOtp(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Boolean.FALSE, response.getBody());
    }
}
