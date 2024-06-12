package com.cadastro.pix.controller;

import com.cadastro.pix.domain.ApiError;
import com.cadastro.pix.domain.User;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(createdUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @Valid @RequestBody User user) {
        try {
            System.out.println("-------------------- chegou aqui ------------------------");
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> pixKeys = userService.getAllUsers();
        return ResponseEntity.ok(pixKeys);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError error = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        ApiError apiError = new ApiError(
                error.getField(),
                error.getDefaultMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}