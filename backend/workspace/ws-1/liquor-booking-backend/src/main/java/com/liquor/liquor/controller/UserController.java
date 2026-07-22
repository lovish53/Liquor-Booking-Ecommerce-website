package com.liquor.liquor.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.liquor.liquor.dto.UserRequest;
import com.liquor.liquor.dto.UserResponse;
import com.liquor.liquor.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRequest request) {

        UserResponse response = userService.registerUser(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){

        return ResponseEntity.ok(userService.getAllUsers());

    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID id){

        return ResponseEntity.ok(
                userService.getUserById(id));

    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest request){

        return ResponseEntity.ok(
                userService.updateUser(id,request));

    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable UUID id){

        userService.deleteUser(id);

        return ResponseEntity.ok("User deleted successfully");

    }
    
    
}
