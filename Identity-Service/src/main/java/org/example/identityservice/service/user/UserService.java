package org.example.identityservice.service.user;

import java.util.List;

import org.example.identityservice.dto.AddBankDTO;
import org.example.identityservice.dto.CheckUserDTO;
import org.example.identityservice.dto.UserDTO;

public interface UserService {
    String checkUser(CheckUserDTO dto);
    UserDTO getById(String id);
    List<UserDTO> getAllUsers(List<String> ids);
    UserDTO addBank(AddBankDTO request);
}
