package com.cadastro.pix.controller;

import com.cadastro.pix.domain.RespDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.service.UserService;
import jakarta.validation.Valid;
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

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<RespDTO> createUser(@Valid @RequestBody User user) {
        try {
            RespDTO respDTO = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping
    public ResponseEntity<RespDTO> findAllUsers() {
        RespDTO respDTO = userService.findAllUsers();
        return ResponseEntity.ok(respDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespDTO> findUserById(@PathVariable UUID id) {
        try {
            RespDTO respDTO = userService.findUserById(id);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody User user) {
        try {
            RespDTO respDTO = userService.updateUser(id, user);
            System.out.println(respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        try {
            RespDTO respDTO =  userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
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
        RespDTO respDTO = new RespDTO(
                HttpStatus.UNPROCESSABLE_ENTITY,
                error.getDefaultMessage()
        );
        return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}