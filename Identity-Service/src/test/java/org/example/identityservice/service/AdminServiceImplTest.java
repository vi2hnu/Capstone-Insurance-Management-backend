package org.example.identityservice.service;

import java.util.List;

import org.example.identityservice.dto.CreateUserDTO;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.exception.UserAlreadyExistsException;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.model.enums.Gender;
import org.example.identityservice.model.enums.Role;
import org.example.identityservice.repository.UsersRepository;
import org.example.identityservice.service.admin.AdminServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private KafkaTemplate<String, Users> kafkaTemplate;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void getUsers_shouldReturnListOfUserDTOs() {
        String roleStr = "USER";
        Users user = new Users();
        user.setId("user-1");
        user.setUsername("testuser");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setGender(Gender.MALE);
        user.setRole(Role.USER);

        when(usersRepository.findAllByRole(roleStr)).thenReturn(List.of(user));

        List<UserDTO> result = adminService.getUsers(roleStr);

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).username());
        assertEquals("Test User", result.get(0).name());
        assertEquals(Role.USER, result.get(0).role());
    }

    @Test
    void createUser_shouldCreateUserAndSendKafkaMessage() {
        CreateUserDTO dto = new CreateUserDTO("New User", "newuser", "new@example.com", Role.ADMIN, Gender.FEMALE);

        when(usersRepository.existsByEmail(dto.email())).thenReturn(false);
        when(usersRepository.existsByUsername(dto.username())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> {
             Users savedUser = invocation.getArgument(0);
             savedUser.setId("123");
             return savedUser;
        });

        Users result = adminService.createUser(dto);

        assertNotNull(result);
        assertEquals("New User", result.getName());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(Role.ADMIN, result.getRole());
        assertNotNull(result.getLastPasswordChange()); 

        verify(usersRepository).save(any(Users.class));

        ArgumentCaptor<Users> kafkaUserCaptor = ArgumentCaptor.forClass(Users.class);
        verify(kafkaTemplate).send(eq("account-activation-email"), kafkaUserCaptor.capture());
        
        Users sentUser = kafkaUserCaptor.getValue();
        assertEquals("newuser", sentUser.getUsername());
        assertNotEquals("encodedPassword", sentUser.getPassword()); 
        assertEquals(10, sentUser.getPassword().length()); 
    }

    @Test
    void createUser_shouldThrowException_whenEmailExists() {
        CreateUserDTO dto = new CreateUserDTO("New User", "newuser", "existing@example.com", Role.USER, Gender.MALE);
        when(usersRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> adminService.createUser(dto));
        verify(usersRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void createUser_shouldThrowException_whenUsernameExists() {
        CreateUserDTO dto = new CreateUserDTO("New User", "existinguser", "new@example.com", Role.USER, Gender.MALE);
        when(usersRepository.existsByEmail(dto.email())).thenReturn(false);
        when(usersRepository.existsByUsername(dto.username())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> adminService.createUser(dto));
        verify(usersRepository, never()).save(any());
    }
}