package org.example.identityservice.service;

import org.example.identityservice.model.entity.Users;
import org.example.identityservice.repository.UsersRepository;
import org.example.identityservice.service.user.UserDetailsServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;


    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        String username = "unknownUser";
        when(usersRepository.findUsersByUsername(username)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername(username)
        );

        verify(usersRepository).findUsersByUsername(username);
    }

    @Test
    void getUserDetails_ShouldReturnUserEntity() {
        String username = "testuser";
        Users user = new Users();
        user.setUsername(username);

        when(usersRepository.findUsersByUsername(username)).thenReturn(user);

        Users result = userDetailsService.getUserDetails(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(usersRepository).findUsersByUsername(username);
    }
}