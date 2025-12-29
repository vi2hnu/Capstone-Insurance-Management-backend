package org.example.identityservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.identityservice.dto.CreateUserDTO;
import org.example.identityservice.dto.UserDTO;
import org.example.identityservice.model.entity.Users;
import org.example.identityservice.service.admin.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/get/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsers(@PathVariable String role){
        log.info("admin has requested users for role {}", role);
        List<UserDTO> users = adminService.getUsers(role);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/create/user")
    public ResponseEntity<Users> createUser(@RequestBody CreateUserDTO dto){
        log.info("admin has requested user creation {}", dto);
        Users createdUser = adminService.createUser(dto);
        return ResponseEntity.status(201).body(createdUser);
    }

}
