package org.example.identityservice.service.auth;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.example.identityservice.dto.ChangePasswordDTO;
import org.example.identityservice.dto.CheckUserDTO;
import org.example.identityservice.dto.GetUserDTO;
import org.example.identityservice.dto.LoginDTO;
import org.example.identityservice.dto.LoginRessultDTO;
import org.example.identityservice.dto.SignupDTO;
import org.example.identityservice.dto.UserDTO;
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
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsersRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager, UsersRepository userRepository,
                       PasswordEncoder encoder, JwtUtils jwtUtils){
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    public LoginRessultDTO login(LoginDTO loginRequest){

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // generate JWT string instead of cookie
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        boolean changePassword = true;

        long diff = new Date().getTime() - userDetails.getLastPasswordChange().getTime();
        if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) < 90) {
            changePassword = false;
        }

        return new LoginRessultDTO(jwtToken, changePassword);
    }


    public boolean signUp(SignupDTO signUpRequest){
        log.info("in service");
        if (userRepository.existsByUsername(signUpRequest.username()) || userRepository.existsByEmail(signUpRequest.email())) {
            log.info("user exists");
            return false;
        }

        // Create new user's account
        Users user = new Users(signUpRequest.name(),signUpRequest.username(),
                signUpRequest.email(),
                encoder.encode(signUpRequest.password()),new Date(),signUpRequest.gender());

        user.setRole(Role.USER);
        userRepository.save(user);
        return true;
    }

    public UserDTO getUser(GetUserDTO dto){
        Users user = userRepository.findUsersByUsername(dto.username());
        return  new UserDTO(user.getUsername(),user.getName(),user.getEmail(),user.getGender(),user.getRole());
    }

    public void changePassword(ChangePasswordDTO request){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.oldPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Users user = userRepository.findUsersByUsername(request.username());
        user.setPassword(encoder.encode(request.newPassword()));
        user.setLastPasswordChange(new Date());
        userRepository.save(user);
    }

    public Users forgotPassword(LoginDTO dto){
        Users user = userRepository.findUsersByUsername(dto.username());
        user.setPassword(encoder.encode(dto.password()));
        return userRepository.save(user);
    }

    public String checkUser(CheckUserDTO dto) {
        Users user = userRepository.findByEmail(dto.email());
        if (user != null) {
            return user.getId();
        } else {
            String username = dto.email().substring(0, dto.email().indexOf('@'));
            Users newUser = new Users(dto.name(), username, dto.email(), encoder.encode(""), new Date(), dto.gender());
            newUser.setRole(Role.USER);
            userRepository.save(newUser);
            return newUser.getId();
        }
    }
}
