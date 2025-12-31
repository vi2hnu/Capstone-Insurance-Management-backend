package org.example.identityservice.service.admin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.example.identityservice.dto.CreateUserDTO;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder encoder;

    public AdminService(UsersRepository usersRepository, PasswordEncoder encoder) {
        this.usersRepository = usersRepository;
        this.encoder = encoder;
    }

    public List<UserDTO> getUsers(String role){
        List<Users> users = usersRepository.findAllByRole(role);
        List<UserDTO> result = new ArrayList<>();
        users.stream()
                .forEach(user -> {
                    result.add(new UserDTO(user.getId(), user.getUsername(),user.getName(),user.getEmail(),user.getGender(),user.getRole(),user.getBankAccount()));
                });
        return result;
    }

    public Users createUser(CreateUserDTO dto){
        if(usersRepository.existsByEmail(dto.email())){
            throw new IllegalArgumentException("Email already exists");
        }

        if(usersRepository.existsByUsername(dto.username())){
            throw new IllegalArgumentException("Username already exists");
        }

        Users user = new Users(dto.name(), dto.username(),
                dto.email(),
                encoder.encode(dto.password()), new Date(), dto.gender());
        user.setRole(dto.role());
        Date oneYearAgo = Date.from(Instant.now().minus(365, ChronoUnit.DAYS));
        user.setLastPasswordChange(oneYearAgo);
        return usersRepository.save(user);
    }


}
