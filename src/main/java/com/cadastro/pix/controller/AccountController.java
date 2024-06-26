package com.cadastro.pix.controller;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.dto.account.CreateAccountDTO;
import com.cadastro.pix.service.AccountServiceImpl;
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
@RequestMapping("/api/account")
@Validated
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountServiceImpl accountService;

    @PostMapping
    public ResponseEntity<RespDTO> createAccount(@Valid @RequestBody CreateAccountDTO accountDTO) {
        logger.info("Request to create account received: {}", accountDTO);
        RespDTO respDTO = accountService.createAccount(accountDTO);
        logger.info("Account created successfully: {}", respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }

    @GetMapping
    public ResponseEntity<RespDTO> findAllAccounts() {
        logger.info("Request to find all accounts received");
        RespDTO respDTO = accountService.findAllAccounts();
        logger.info("Accounts retrieved successfully: {}", respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespDTO> findAccountById(@PathVariable UUID id) {
        logger.info("Request to find account by id received: {}", id);
        RespDTO respDTO = accountService.findAccountById(id);
        logger.info("Account retrieved successfully for id {}: {}", id, respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespDTO> updateAccount(@PathVariable UUID id, @Valid @RequestBody Account account) {
        logger.info("Request to update account received for id {}: {}", id, account);
        RespDTO respDTO = accountService.updateAccount(id, account);
        logger.info("Account updated successfully for id {}: {}", id, respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RespDTO> deleteAccount(@PathVariable UUID id) {
        logger.info("Request to delete account received for id {}", id);
        RespDTO respDTO =  accountService.deleteAccount(id);
        logger.info("Account deleted successfully for id {}: {}", id, respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }
}