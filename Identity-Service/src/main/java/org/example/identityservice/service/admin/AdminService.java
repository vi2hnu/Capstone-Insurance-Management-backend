package org.example.identityservice.service.admin;

import java.util.List;

import org.example.identityservice.dto.CreateUserDTO;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.model.entity.Users;

public interface AdminService {
    List<UserDTO> getUsers(String role);
    Users createUser(CreateUserDTO dto);
}
