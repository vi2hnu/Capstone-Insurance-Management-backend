package org.example.identityservice.service;

import java.util.List;
import java.util.Optional;

import org.example.identityservice.dto.AddBankDTO;
import org.example.identityservice.dto.CheckUserDTO;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.exception.UsersNotFoundException;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.model.enums.Gender;
import org.example.identityservice.model.enums.Role;
import org.example.identityservice.repository.UsersRepository;
import org.example.identityservice.service.user.UserServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UsersRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void checkUser_whenUserExists_returnsExistingId() {
        CheckUserDTO dto = new CheckUserDTO("John Doe", "john@example.com", Gender.MALE);
        Users existingUser = new Users();
        existingUser.setId("user-123");
        existingUser.setEmail("john@example.com");

        when(userRepository.findByEmail(dto.email())).thenReturn(existingUser);

        String result = userService.checkUser(dto);

        assertEquals("user-123", result);
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void checkUser_whenUserDoesNotExist_createsNewUser() {
        CheckUserDTO dto = new CheckUserDTO("Jane Doe", "jane@example.com", Gender.FEMALE);
        
        when(userRepository.findByEmail(dto.email())).thenReturn(null);
        when(encoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users u = invocation.getArgument(0);
            u.setId("new-id-456");
            return u;
        });

        String result = userService.checkUser(dto);

        assertEquals("new-id-456", result);
        verify(userRepository).save(argThat(user -> 
            user.getEmail().equals("jane@example.com") &&
            user.getUsername().equals("jane") &&
            user.getRole() == Role.USER
        ));
    }

    @Test
    void getById_whenUserFound_returnsUserDTO() {
        String userId = "user-123";
        Users user = new Users();
        user.setId(userId);
        user.setUsername("john");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setGender(Gender.MALE);
        user.setRole(Role.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDTO result = userService.getById(userId);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("john", result.username());
    }

    @Test
    void getById_whenUserNotFound_returnsNull() {
        when(userRepository.findById("invalid-id")).thenReturn(Optional.empty());

        UserDTO result = userService.getById("invalid-id");

        assertNull(result);
    }

    @Test
    void getAllUsers_returnsListOfUserDTOs() {
        List<String> ids = List.of("1", "2");
        Users u1 = new Users(); 
        u1.setId("1");
        Users u2 = new Users(); 
        u2.setId("2");

        when(userRepository.findAllById(ids)).thenReturn(List.of(u1, u2));

        List<UserDTO> result = userService.getAllUsers(ids);

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).id());
        assertEquals("2", result.get(1).id());
    }

    @Test
    void addBank_whenUserExists_updatesAndReturnsUserDTO() {
        AddBankDTO request = new AddBankDTO("user-123", "HDFC", "123456789", "HDFC001");
        Users user = new Users();
        user.setId("user-123");
        user.setUsername("testuser");
        
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(userRepository.save(any(Users.class))).thenReturn(user);

        UserDTO result = userService.addBank(request);

        assertNotNull(result.bankAccount());
        assertEquals("HDFC", result.bankAccount().getBankName());
        assertEquals("123456789", result.bankAccount().getAccountNumber());
        verify(userRepository).save(user);
    }

    @Test
    void addBank_whenUserNotFound_throwsException() {
        AddBankDTO request = new AddBankDTO("invalid-id", "HDFC", "123", "CODE");
        
        when(userRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(UsersNotFoundException.class, () -> userService.addBank(request));
        verify(userRepository, never()).save(any());
    }
}