package com.cadastro.pix.service;

import com.cadastro.pix.domain.*;
import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.dto.account.CreateAccountDTO;
import com.cadastro.pix.dto.account.SimpleAccountListWithUserDTO;
import com.cadastro.pix.dto.account.SimpleAccountWithUserDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public RespDTO createAccount(@Valid CreateAccountDTO newAccountDTO) {
        log.info("Starting account creation process for request: {}", newAccountDTO);

        User user = userRepository.findByIdentification(newAccountDTO.getIdentification());

        if (user == null) {
            log.error("User not found with identification: {}", newAccountDTO.getIdentification());
            throw new EntityNotFoundException("User not found with this identification");
        }

        Account account = new Account(newAccountDTO);

        log.info("Validating account: {}", account);
        validateCreateAccount(account);
        log.info("Account validation successful");

        account.setUser(user);
        account.setActive(true);

        log.info("Saving account: {}", account);
        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(accountRepository.save(account).getId());
        log.info("Account created successfully: {}", accountDTO);
        return new RespDTO(HttpStatus.OK, accountDTO);
    }

    @Transactional
    public RespDTO updateAccount(UUID id, Account account) {
        log.info("Starting account update process for id: {}", id);

        Account existingAccount = accountRepository.findById(id);
        if (existingAccount == null) {
            log.error("Account not found with id: {}", id);
            throw new EntityNotFoundException("Account not found");
        }

        if (!existingAccount.isActive()) {
            log.error("Attempt to update inactive account with id: {}", id);
            throw new IllegalArgumentException("This account is inactive");
        }

        log.info("Validating fields for account update: {}", account);
        validateUpdatedFields(account);

        existingAccount.setAccountType(account.getAccountType());
        existingAccount.setAgencyNumber(account.getAgencyNumber());
        existingAccount.setAccountNumber(account.getAccountNumber());

        log.info("Saving updated account: {}", existingAccount);
        Account updatedAccount = accountRepository.save(existingAccount);
        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(updatedAccount);
        log.info("Account updated successfully: {}", accountDTO);
        return new RespDTO(HttpStatus.OK, accountDTO);
    }

    @Transactional
    public RespDTO deleteAccount(UUID id) {
        log.info("Starting account deletion process for id: {}", id);

        Account existingAccount = accountRepository.findById(id);
        if (existingAccount == null) {
            log.error("Account not found with id: {}", id);
            throw new EntityNotFoundException("Account not found");
        }

        if (!existingAccount.isActive()) {
            log.error("Attempt to delete already inactive account with id: {}", id);
            throw new IllegalArgumentException("This account is already inactive");
        }

        log.info("Inactivating account: {}", existingAccount);
        existingAccount.setActive(false);
        existingAccount.setInactivatedAt(LocalDateTime.now());

        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(accountRepository.save(existingAccount));
        log.info("Account inactivated successfully: {}", accountDTO);
        return new RespDTO(HttpStatus.OK, accountDTO);
    }

    public RespDTO findAllAccounts() {
        log.info("Starting process to find all accounts");

        List<Account> accounts = accountRepository.findAll();

        if (accounts.isEmpty()) {
            log.error("No Accounts found");
            throw new EntityNotFoundException("No Accounts found");
        }
        log.info("Number of accounts found: {}", accounts.size());

        SimpleAccountListWithUserDTO accountListDTO = SimpleAccountListWithUserDTO.fromAccounts(accounts);
        log.info("Accounts retrieved successfully");
        return new RespDTO(HttpStatus.OK, accountListDTO);
    }

    public RespDTO findAccountById(UUID id) {
        log.info("Starting process to find account by id: {}", id);

        Account existingAccount = accountRepository.findById(id);
        if (existingAccount == null) {
            log.error("Account not found with id: {}", id);
            throw new EntityNotFoundException("Account not found");
        }

        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(existingAccount);
        log.info("Account retrieved successfully: {}", accountDTO);
        return new RespDTO(HttpStatus.OK, accountDTO);
    }

    private void validateCreateAccount(Account account) {
        log.info("Validating account creation fields for account: {}", account);
        validateExistAccount(account);
        validateAccountType(account.getAccountType());
        validateAgencyNumber(account.getAgencyNumber());
        validateAccountNumber(account.getAccountNumber());
        log.info("Account creation fields validated successfully");
    }

    private void validateUpdatedFields(Account account) {
        log.info("Validating account update fields for account: {}", account);
        validateAccountType(account.getAccountType());
        validateAgencyNumber(account.getAgencyNumber());
        validateAccountNumber(account.getAccountNumber());
        log.info("Account update fields validated successfully");
    }

    private void validateAccountType(String tipoConta) {
        if (!tipoConta.equalsIgnoreCase("corrente") && !tipoConta.equalsIgnoreCase("poupança")) {
            log.error("Invalid account type: {}", tipoConta);
            throw new IllegalArgumentException("Invalid account type");
        }
    }

    private void validateAgencyNumber(Integer numeroAgencia) {
        if (numeroAgencia == null || numeroAgencia.toString().length() > 4) {
            log.error("Invalid agency number: {}", numeroAgencia);
            throw new IllegalArgumentException("Invalid agency number");
        }
    }

    private void validateAccountNumber(Integer numeroConta) {
        if (numeroConta == null || numeroConta.toString().length() > 8) {
            log.error("Invalid account number: {}", numeroConta);
            throw new IllegalArgumentException("Invalid account number");
        }
    }

    private void validateExistAccount(Account account) {
        log.info("Checking if account already exists: {}", account);
        Account existAccount = accountRepository.findByAgencyNumberAndAccountNumber(
                account.getAgencyNumber(), account.getAccountNumber()
        );

        if (existAccount != null) {
            if(existAccount.isActive()) {
                log.error("Active account already exists with account number {} at agency {}", account.getAccountNumber(), account.getAgencyNumber());
                throw new IllegalArgumentException("There is already an account with that account number at this agency");
            }

            log.error("Inactive account already exists with account number {} at agency {}", account.getAccountNumber(), account.getAgencyNumber());
            throw new IllegalArgumentException("There is already an inactive account with that account number at this agency");
        }
        log.info("No existing account found with account number {} at agency {}", account.getAccountNumber(), account.getAgencyNumber());
    }
}