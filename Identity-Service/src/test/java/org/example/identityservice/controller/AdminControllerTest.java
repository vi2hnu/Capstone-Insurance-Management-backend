package org.example.identityservice.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.example.identityservice.dto.CreateUserDTO;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.service.admin.AdminService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void getUsers_ShouldReturnListOfUsers_WhenRoleIsProvided() {
        String role = "ROLE_USER";

        UserDTO user1 = new UserDTO("1", "user1", "User One", "u1@test.com", null, null, null);
        UserDTO user2 = new UserDTO("2", "user2", "User Two", "u2@test.com", null, null, null);

        List<UserDTO> mockUsers = Arrays.asList(user1, user2);

        when(adminService.getUsers(role)).thenReturn(mockUsers);

        ResponseEntity<List<UserDTO>> response = adminController.getUsers(role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(adminService, times(1)).getUsers(role);
    }

    @Test
    void getUsers_ShouldReturnEmptyList_WhenNoUsersFound() {
        String role = "ROLE_UNKNOWN";
        when(adminService.getUsers(role)).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserDTO>> response = adminController.getUsers(role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void createUser_ShouldReturnCreatedUser_WhenDtoIsValid() {
        CreateUserDTO createUserDTO = new CreateUserDTO(
            "Test Name",
            "testuser",
            "test@test.com",
            null,
            null
        );

        Users createdUser = new Users();
        createdUser.setId("unique-id-123");
        createdUser.setEmail("test@test.com");

        when(adminService.createUser(createUserDTO)).thenReturn(createdUser);

        ResponseEntity<Users> response = adminController.createUser(createUserDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdUser, response.getBody());
        assertEquals("unique-id-123", response.getBody().getId());

        verify(adminService, times(1)).createUser(createUserDTO);
    }
}
