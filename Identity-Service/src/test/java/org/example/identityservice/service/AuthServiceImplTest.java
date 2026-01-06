package org.example.identityservice.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.example.identityservice.dto.ChangePasswordDTO;
import org.example.identityservice.dto.ForgotPasswordDTO;
import org.example.identityservice.dto.LoginDTO;
import org.example.identityservice.dto.LoginRessultDTO;
import org.example.identityservice.dto.SignupDTO;
import org.example.identityservice.exception.UserAlreadyExistsException;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.model.enums.Gender;
import org.example.identityservice.model.enums.Role;
import org.example.identityservice.repository.UsersRepository;
import org.example.identityservice.service.auth.AuthServiceImpl;
import org.example.identityservice.service.jwt.JwtUtils;
import org.example.identityservice.service.user.UserDetailsImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsersRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_whenCredentialsValid_returnsTokenAndDto() {
        
        LoginDTO loginRequest = new LoginDTO("john_doe", "password123");
        Authentication authentication = mock(Authentication.class);
        
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn("john_doe");
        when(userDetails.getId()).thenReturn("user-id-123"); // ID is now String
        when(userDetails.getEmail()).thenReturn("john@example.com");
        when(userDetails.getRole()).thenReturn(Role.USER);
        
        when(userDetails.getLastPasswordChange()).thenReturn(new Date());

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateTokenFromUsername("john_doe")).thenReturn("mock-jwt-token");

        LoginRessultDTO result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("mock-jwt-token", result.token()); 
        assertFalse(result.changePassword());
        
        assertEquals("user-id-123", result.user().id());
        assertEquals("john_doe", result.user().username());
        assertEquals("john@example.com", result.user().email());
        assertEquals(Role.USER, result.user().role());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_whenPasswordExpired_returnsChangePasswordTrue() {
        
        LoginDTO loginRequest = new LoginDTO("john_doe", "password123");
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        Date oldDate = Date.from(Instant.now().minus(100, ChronoUnit.DAYS));
        
        when(userDetails.getUsername()).thenReturn("john_doe");
        when(userDetails.getId()).thenReturn("user-id-123");
        when(userDetails.getLastPasswordChange()).thenReturn(oldDate);
        
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateTokenFromUsername("john_doe")).thenReturn("token");

        LoginRessultDTO result = authService.login(loginRequest);

        assertTrue(result.changePassword());
    }

    @Test
    void signUp_whenNewUser_createsAccount() {
        
        SignupDTO signUpRequest = new SignupDTO("John", "john_doe", "john@test.com", "pass", Gender.MALE);

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(encoder.encode("pass")).thenReturn("encodedPass");
        when(userRepository.save(any(Users.class))).thenAnswer(i -> i.getArguments()[0]);

        boolean result = authService.signUp(signUpRequest);

        assertTrue(result);
        verify(userRepository).save(argThat(user -> 
            user.getUsername().equals("john_doe") && 
            user.getRole() == Role.USER &&
            user.getPassword().equals("encodedPass") &&
            user.getGender() == Gender.MALE
        ));
    }

    @Test
    void signUp_whenUserExists_throwsException() {
        
        SignupDTO signUpRequest = new SignupDTO("John", "john_doe", "john@test.com", "pass", Gender.MALE);
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        
        assertThrows(UserAlreadyExistsException.class, () -> authService.signUp(signUpRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_verifiesOldAndSavesNew() {
        
        ChangePasswordDTO request = new ChangePasswordDTO("john_doe", "oldPass", "newPass");
        Authentication authentication = mock(Authentication.class);
        Users user = new Users();
        user.setUsername("john_doe");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findUsersByUsername("john_doe")).thenReturn(user);
        when(encoder.encode("newPass")).thenReturn("encodedNewPass");

        authService.changePassword(request);

        assertEquals("encodedNewPass", user.getPassword());
        assertNotNull(user.getLastPasswordChange());
        verify(userRepository).save(user);
    }

    @Test
    void forgotPassword_updatesPasswordWithoutAuth() {
        ForgotPasswordDTO request = new ForgotPasswordDTO("john@test.com", "resetPass");
        Users user = new Users();
        user.setEmail("john@test.com");

        when(userRepository.findUsersByEmail("john@test.com")).thenReturn(user);
        when(encoder.encode("resetPass")).thenReturn("encodedReset");
        when(userRepository.save(any(Users.class))).thenReturn(user);

        Users result = authService.forgotPassword(request);

        assertEquals("encodedReset", result.getPassword());
        verify(userRepository).save(user);
    }
}