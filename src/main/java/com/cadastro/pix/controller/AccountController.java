package com.cadastro.pix.controller;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.ReqObj;
import com.cadastro.pix.domain.RespDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.service.AccountService;
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

@RestController
@RequestMapping("/api/account")
@Validated
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<RespDTO> createAccount(@Valid @RequestBody ReqObj reqObj) {
        try {
            RespDTO respDTO = accountService.createAccount(reqObj);
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
    public ResponseEntity<RespDTO> findAllAccounts() {
        try {
            RespDTO respDTO = accountService.findAllAccounts();
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

    @GetMapping("/{id}")
    public ResponseEntity<RespDTO> findAccountById(@PathVariable UUID id) {
        try {
            RespDTO respDTO = accountService.findAccountById(id);
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
    public ResponseEntity<RespDTO> updateAccount(@PathVariable String id, @Valid @RequestBody Account account) {
        try {
            RespDTO respDTO = accountService.updateAccount(id, account);
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
    public ResponseEntity<RespDTO> deleteAccount(@PathVariable UUID id) {
        try {
            RespDTO respDTO =  accountService.deleteAccount(id);
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