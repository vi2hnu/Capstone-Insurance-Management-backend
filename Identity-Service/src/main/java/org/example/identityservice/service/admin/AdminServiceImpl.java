package org.example.identityservice.service.admin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.example.identityservice.dto.CreateUserDTO;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.repository.UsersRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder encoder;
    private final KafkaTemplate<String, Users> kafkaTemplate;

    public AdminServiceImpl(UsersRepository usersRepository, PasswordEncoder encoder, KafkaTemplate<String, Users> kafkaTemplate) {
        this.usersRepository = usersRepository;
        this.encoder = encoder;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Source - https://stackoverflow.com/a
// Posted by Suresh Atta, modified by community. See post 'Timeline' for change history
// Retrieved 2026-01-02, License - CC BY-SA 3.0
    private String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    @Override
    public List<UserDTO> getUsers(String role) {
        List<Users> users = usersRepository.findAllByRole(role);
        List<UserDTO> result = new ArrayList<>();
        users.stream()
                .forEach(user -> {
                    result.add(new UserDTO(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getGender(), user.getRole(), user.getBankAccount()));
                });
        return result;
    }

    @Override
    public Users createUser(CreateUserDTO dto) {
        if (usersRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (usersRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        String password = getRandomString();
        Users user = new Users(dto.name(), dto.username(),
                dto.email(), encoder.encode(password), new Date(), dto.gender());
        user.setRole(dto.role());
        Date oneYearAgo = Date.from(Instant.now().minus(365, ChronoUnit.DAYS));
        user.setLastPasswordChange(oneYearAgo);

        usersRepository.save(user);
        Users tempUser = new Users(dto.name(), dto.username(),
                dto.email(), password, new Date(), dto.gender());
        kafkaTemplate.send("account-activation-email", tempUser);
        return user;
    }

}
