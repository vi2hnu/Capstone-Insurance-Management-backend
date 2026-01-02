package org.example.identityservice.controller;

import org.example.identityservice.dto.ChangePasswordDTO;
import org.example.identityservice.dto.ForgotPasswordDTO;
import org.example.identityservice.dto.LoginDTO;
import org.example.identityservice.dto.MessageResponse;
import org.example.identityservice.dto.SignupDTO;
import org.example.identityservice.dto.ValidateOtpDTO;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.service.auth.AuthService;
import org.example.identityservice.service.jwt.JwtUtils;
import org.example.identityservice.service.otp.OtpService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final OtpService otpService;

    public AuthController(AuthService authService,JwtUtils jwtUtils, OtpService otpService){
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.otpService = otpService;
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }


    @PostMapping("/signUp")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupDTO signUpRequest) {
        if(!authService.signUp(signUpRequest)){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: account already exist"));
        }
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signOut")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @PostMapping("/change/password")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordDTO request){
        authService.changePassword(request);
        return ResponseEntity.ok(new MessageResponse("User password has been changed. Please login again"));
    }

    @PostMapping("/forgot/password")
    public ResponseEntity<Users> forgotPassword(@RequestBody ForgotPasswordDTO request){
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/otp/generate/{email}")
    public ResponseEntity<String> generateOtp(@PathVariable String email){
        return ResponseEntity.ok(otpService.generateOtp(email));
    }

    @PostMapping("/otp/validate")
    public ResponseEntity<Boolean> validateOtp(@RequestBody ValidateOtpDTO request){
        Boolean isValid = otpService.verifyOtp(request);
        if(isValid){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.badRequest().body(false);
    }

}