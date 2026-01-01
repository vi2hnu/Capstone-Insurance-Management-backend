package org.example.identityservice.controller;

import java.util.List;

import org.example.identityservice.dto.AddBankDTO;
import org.example.identityservice.dto.CheckUserDTO;
import org.example.identityservice.dto.MessageResponse;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/check/user")  //used to create or get existing users id
    public ResponseEntity<String> checkUser(@RequestBody CheckUserDTO dto) {
        return ResponseEntity.ok(userService.checkUser(dto));
    }

    @GetMapping("/get/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping("/get/user-list")
    public ResponseEntity<List<UserDTO>> getUserList(@RequestBody List<String> ids) {
        return ResponseEntity.ok(userService.getAllUsers(ids));
    }

    @PostMapping("/add/bank")
    public ResponseEntity<MessageResponse> addBank(@RequestBody AddBankDTO request){
        userService.addBank(request);
        return ResponseEntity.ok(new MessageResponse("User bank details has been added"));
    }
    
}
