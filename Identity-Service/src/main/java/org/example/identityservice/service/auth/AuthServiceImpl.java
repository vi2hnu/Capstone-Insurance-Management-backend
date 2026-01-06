package org.example.identityservice.service.auth;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.example.identityservice.dto.ChangePasswordDTO;
import org.example.identityservice.dto.ForgotPasswordDTO;
import org.example.identityservice.dto.LoginDTO;
import org.example.identityservice.dto.LoginRessultDTO;
import org.example.identityservice.dto.SignupDTO;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.exception.UserAlreadyExistsException;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.model.enums.Role;
import org.example.identityservice.repository.UsersRepository;
import org.example.identityservice.service.jwt.JwtUtils;
import org.example.identityservice.service.user.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsersRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UsersRepository userRepository,
                       PasswordEncoder encoder, JwtUtils jwtUtils){
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public LoginRessultDTO login(LoginDTO loginRequest){

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails.getUsername());


        boolean changePassword = true;

        long diff = new Date().getTime() - userDetails.getLastPasswordChange().getTime();
        if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) < 90) {
            changePassword = false;
        }
        UserDTO userDTO = new UserDTO(userDetails.getId(),userDetails.getUsername(),null, userDetails.getEmail(),
                null,userDetails.getRole(),null);
        return new LoginRessultDTO(jwtToken, changePassword, userDTO);
    }

    @Override
    public boolean signUp(SignupDTO signUpRequest){
        log.info("in service");
        if (userRepository.existsByUsername(signUpRequest.username()) || userRepository.existsByEmail(signUpRequest.email())) {
            log.info("user exists");
            throw new UserAlreadyExistsException("Username or Email already exists");
        }

        // Create new user's account
        Users user = new Users(signUpRequest.name(),signUpRequest.username(),
                signUpRequest.email(),
                encoder.encode(signUpRequest.password()),new Date(),signUpRequest.gender());

        user.setRole(Role.USER);
        userRepository.save(user);
        return true;
    }

    @Override
    public void changePassword(ChangePasswordDTO request){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.oldPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Users user = userRepository.findUsersByUsername(request.username());
        user.setPassword(encoder.encode(request.newPassword()));
        user.setLastPasswordChange(new Date());
        userRepository.save(user);
    }

    @Override
    public Users forgotPassword(ForgotPasswordDTO dto){
        Users user = userRepository.findUsersByEmail(dto.email());
        user.setPassword(encoder.encode(dto.password()));
        return userRepository.save(user);
    }
}
