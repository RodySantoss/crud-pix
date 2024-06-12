package com.cadastro.pix.controller;

import com.cadastro.pix.domain.Account;
import com.cadastro.pix.domain.PixKey;
import com.cadastro.pix.domain.ReqObj;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<?> createAccount(@Valid @RequestBody ReqObj reqObj) {
        try {
            Account createdAccount = accountService.createAccount(reqObj);
            return ResponseEntity.status(HttpStatus.OK).body(createdAccount.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable String id, @Valid @RequestBody Account account) {
        try {
            Account updatedAccount = accountService.updateAccount(id, account);
            return ResponseEntity.status(HttpStatus.OK).body(updatedAccount);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllPixKeys() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getPixKeyById(@PathVariable UUID id) {
//        return pixKeyService.getPixKeyById(id);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deletePixKey(@PathVariable UUID id) {
//        try {
//            return pixKeyService.deletePixKey(id);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
//        }
//    }
}