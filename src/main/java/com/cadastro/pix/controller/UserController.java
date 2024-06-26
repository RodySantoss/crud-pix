package com.cadastro.pix.controller;

import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserServiceImpl userService;

    @PostMapping
    public ResponseEntity<RespDTO> createUser(@Valid @RequestBody User user) {
        logger.info("Request to create user received: {}", user);
        RespDTO respDTO = userService.createUser(user);
        logger.info("User created successfully: {}", respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }

    @GetMapping
    public ResponseEntity<RespDTO> findAllUsers() {
        logger.info("Request to find all users received");
        RespDTO respDTO = userService.findAllUsers();
        logger.info("Users retrieved successfully: {}", respDTO);
        return ResponseEntity.ok(respDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespDTO> findUserById(@PathVariable UUID id) {
        logger.info("Request to find user by id received: {}", id);
        RespDTO respDTO = userService.findUserById(id);
        logger.info("User retrieved successfully for id {}: {}", id, respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody User user) {
        logger.info("Request to update user received for id {}: {}", id, user);
        RespDTO respDTO = userService.updateUser(id, user);
        logger.info("User updated successfully for id {}: {}", id, respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RespDTO> deleteUser(@PathVariable UUID id) {
        logger.info("Request to delete user received for id {}", id);
        RespDTO respDTO = userService.deleteUser(id);
        logger.info("User deleted successfully for id {}: {}", id, respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }
}