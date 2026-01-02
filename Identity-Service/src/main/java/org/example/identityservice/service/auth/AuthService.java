package org.example.identityservice.service.auth;

import org.example.identityservice.dto.ChangePasswordDTO;
import org.example.identityservice.dto.ForgotPasswordDTO;
import org.example.identityservice.dto.LoginDTO;
import org.example.identityservice.dto.LoginRessultDTO;
import org.example.identityservice.dto.SignupDTO;
import org.example.identityservice.model.entity.Users;

public interface AuthService {
    boolean signUp(SignupDTO dto);
    LoginRessultDTO login(LoginDTO dto);
    void changePassword(ChangePasswordDTO dto);
    Users forgotPassword(ForgotPasswordDTO dto);
}
