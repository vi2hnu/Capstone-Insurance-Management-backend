package org.example.identityservice.controller;

import java.util.List;

import org.example.identityservice.dto.AddBankDTO;
import org.example.identityservice.dto.ChangePasswordDTO;
import org.example.identityservice.dto.CheckUserDTO;
import org.example.identityservice.dto.GenerateOtpDTO;
import org.example.identityservice.dto.GetUserDTO;
import org.example.identityservice.dto.LoginDTO;
import org.example.identityservice.dto.MessageResponse;
import org.example.identityservice.dto.SignupDTO;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.dto.ValidateOtpDTO;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.service.auth.AuthService;
import org.example.identityservice.service.jwt.JwtUtils;
import org.example.identityservice.service.otp.OtpService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/user")
    public ResponseEntity<UserDTO> getUser(@RequestBody GetUserDTO dto) {
        return ResponseEntity.ok(authService.getUser(dto));
    }

    @PostMapping("/change/password")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordDTO request){
        authService.changePassword(request);
        return ResponseEntity.ok(new MessageResponse("User password has been changed. Please login again"));
    }

    @PostMapping("/forgot/password")
    public ResponseEntity<Users> forgotPassword(@RequestBody LoginDTO request){
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/otp/generate")
    public ResponseEntity<String> generateOtp(@RequestBody GenerateOtpDTO request){
        return ResponseEntity.ok(otpService.generateOtp(request));
    }

    @PostMapping("/otp/validate")
    public ResponseEntity<Boolean> validateOtp(@RequestBody ValidateOtpDTO request){
        Boolean isValid = otpService.verifyOtp(request);
        if(isValid){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.badRequest().body(false);
    }

    @PostMapping("/check/user")
    public ResponseEntity<String> checkUser(@RequestBody CheckUserDTO dto) {
        return ResponseEntity.ok(authService.checkUser(dto));
    }

    @GetMapping("/get/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(authService.getById(id));
    }

    @PostMapping("/get/user-list")
    public ResponseEntity<List<UserDTO>> getUserList(@RequestBody List<String> ids) {
        return ResponseEntity.ok(authService.getAllUsers(ids));
    }

    @PostMapping("/add/bank")
    public ResponseEntity<MessageResponse> addBank(@RequestBody AddBankDTO request){
        authService.addBank(request);
        return ResponseEntity.ok(new MessageResponse("User bank details has been added"));
    }

}