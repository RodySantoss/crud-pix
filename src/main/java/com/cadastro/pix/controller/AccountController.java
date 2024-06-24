package com.cadastro.pix.controller;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.dto.account.CreateAccountDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.service.AccountService;
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
@RequestMapping("/api/account")
@Validated
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<RespDTO> createAccount(@Valid @RequestBody CreateAccountDTO accountDTO) {
        log.info("Request to create account received: {}", accountDTO);
        try {
            RespDTO respDTO = accountService.createAccount(accountDTO);
            log.info("Account created successfully: {}", respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while creating account: {}", e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while creating account: {}", e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping
    public ResponseEntity<RespDTO> findAllAccounts() {
        log.info("Request to find all accounts received");
        try {
            RespDTO respDTO = accountService.findAllAccounts();
            log.info("Accounts retrieved successfully: {}", respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while finding all accounts: {}", e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespDTO> findAccountById(@PathVariable UUID id) {
        log.info("Request to find account by id received: {}", id);
        try {
            RespDTO respDTO = accountService.findAccountById(id);
            log.info("Account retrieved successfully for id {}: {}", id, respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while finding account by id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while finding account by id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespDTO> updateAccount(@PathVariable UUID id, @Valid @RequestBody Account account) {
        log.info("Request to update account received for id {}: {}", id, account);
        try {
            RespDTO respDTO = accountService.updateAccount(id, account);
            log.info("Account updated successfully for id {}: {}", id, respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while updating account for id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while updating account for id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RespDTO> deleteAccount(@PathVariable UUID id) {
        log.info("Request to delete account received for id {}", id);
        try {
            RespDTO respDTO =  accountService.deleteAccount(id);
            log.info("Account deleted successfully for id {}: {}", id, respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while deleting account for id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while deleting account for id {}: {}", id, e.getMessage());
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