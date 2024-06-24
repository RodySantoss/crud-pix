package com.cadastro.pix.controller;

import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<RespDTO> createUser(@Valid @RequestBody User user) {
        log.info("Request to create user received: {}", user);
        try {
            RespDTO respDTO = userService.createUser(user);
            log.info("User created successfully: {}", respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while creating user: {}", e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping
    public ResponseEntity<RespDTO> findAllUsers() {
        log.info("Request to find all users received");
        try {
            RespDTO respDTO = userService.findAllUsers();
            log.info("Users retrieved successfully: {}", respDTO);
            return ResponseEntity.ok(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while finding all users: {}", e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespDTO> findUserById(@PathVariable UUID id) {
        log.info("Request to find user by id received: {}", id);
        try {
            RespDTO respDTO = userService.findUserById(id);
            log.info("User retrieved successfully for id {}: {}", id, respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while finding user by id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody User user) {
        log.info("Request to update user received for id {}: {}", id, user);
        try {
            RespDTO respDTO = userService.updateUser(id, user);
            log.info("User updated successfully for id {}: {}", id, respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while updating user for id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while updating user for id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RespDTO> deleteUser(@PathVariable UUID id) {
        log.info("Request to delete user received for id {}", id);
        try {
            RespDTO respDTO = userService.deleteUser(id);
            log.info("User deleted successfully for id {}: {}", id, respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while deleting user for id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while deleting user for id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError error = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        log.error("Validation error: {}", error.getDefaultMessage());
        RespDTO respDTO = new RespDTO(
                HttpStatus.UNPROCESSABLE_ENTITY,
                error.getDefaultMessage()
        );
        return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}