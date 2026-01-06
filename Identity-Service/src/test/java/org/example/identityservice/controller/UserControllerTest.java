package org.example.identityservice.controller;

import java.util.Arrays;
import java.util.List;

import org.example.identityservice.dto.AddBankDTO;
import org.example.identityservice.dto.CheckUserDTO;
import org.example.identityservice.dto.MessageResponse;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.service.user.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void checkUser_ShouldReturnUserId() {
        CheckUserDTO dto = new CheckUserDTO("Test Name", "test@test.com", null);
        String expectedUserId = "user-123";
        
        when(userService.checkUser(dto)).thenReturn(expectedUserId);

        ResponseEntity<String> response = userController.checkUser(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserId, response.getBody());
        verify(userService, times(1)).checkUser(dto);
    }

    @Test
    void getUserById_ShouldReturnUserDTO() {
        String userId = "user-123";
        UserDTO userDto = new UserDTO(userId, "username", "name", "email", null, null, null);

        when(userService.getById(userId)).thenReturn(userDto);

        ResponseEntity<UserDTO> response = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(userService, times(1)).getById(userId);
    }

    @Test
    void getUserList_ShouldReturnListOfUserDTOs() {
        List<String> ids = Arrays.asList("1", "2", "3");
        UserDTO u1 = new UserDTO("1", "u1", "n1", "e1", null, null, null);
        UserDTO u2 = new UserDTO("2", "u2", "n2", "e2", null, null, null);
        List<UserDTO> userList = Arrays.asList(u1, u2);

        when(userService.getAllUsers(ids)).thenReturn(userList);

        ResponseEntity<List<UserDTO>> response = userController.getUserList(ids);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(userList, response.getBody());
        verify(userService, times(1)).getAllUsers(ids);
    }

    @Test
    void addBank_ShouldReturnSuccessMessage() {
        AddBankDTO request = mock(AddBankDTO.class); 

        ResponseEntity<MessageResponse> response = userController.addBank(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User bank details has been added", response.getBody().getMessage());
        verify(userService, times(1)).addBank(request);
    }
}