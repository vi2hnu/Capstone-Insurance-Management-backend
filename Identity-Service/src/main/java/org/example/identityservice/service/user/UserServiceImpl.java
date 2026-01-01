package org.example.identityservice.service.user;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.example.identityservice.dto.AddBankDTO;
import org.example.identityservice.dto.CheckUserDTO;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.exception.UsersNotFoundException;
import org.example.identityservice.model.entity.BankAccount;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.model.enums.Role;
import org.example.identityservice.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository userRepository;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UsersRepository userRepository, PasswordEncoder encoder){
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
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

    @Override
    public UserDTO getById(String id) {
        Users user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        return new UserDTO(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getGender(), user.getRole(), user.getBankAccount());
    }
    
    @Override
    public List<UserDTO> getAllUsers(List<String> ids) {
        List<Users> users = userRepository.findAllById(ids);
        return users.stream()
            .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getGender(), user.getRole(), user.getBankAccount()))
            .collect(Collectors.toList());
    }

    @Override
    public UserDTO addBank(AddBankDTO request) {
        Users user = userRepository.findById(request.userId()).orElse(null);
        if (user == null) {
            throw new UsersNotFoundException("User not found");
        }
        BankAccount bankAccount = new BankAccount(request.bankName(), request.accountNumber(), request.ifscCode());
        user.setBankAccount(bankAccount);
        userRepository.save(user);
        return new UserDTO(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getGender(), user.getRole(), user.getBankAccount());
    }
}
