package com.cadastro.pix.service;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.dto.account.CreateAccountDTO;
import com.cadastro.pix.dto.account.SimpleAccountListWithUserDTO;
import com.cadastro.pix.dto.account.SimpleAccountWithUserDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.interfaces.services.AccountService;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.UserRepository;
import com.cadastro.pix.utils.Validate;
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
public class AccountServiceImpl implements AccountService {

    private final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validate validate;

    @Transactional
    public RespDTO createAccount(@Valid CreateAccountDTO newAccountDTO) {
        logger.info("Starting account creation process for request: {}", newAccountDTO);

        User user = userRepository.findByIdentification(newAccountDTO.getIdentification());

        if (user == null) {
            logger.error("User not found with identification: {}", newAccountDTO.getIdentification());
            throw new EntityNotFoundException("User not found with this identification");
        }

        Account account = new Account(newAccountDTO);

        logger.info("Validating account: {}", account);
        validate.validateCreateAccount(account);
        logger.info("Account validation successful");

        account.setUser(user);
        account.setActive(true);

        logger.info("Saving account: {}", account);
        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(accountRepository.save(account).getId());
        logger.info("Account created successfully: {}", accountDTO);
        return new RespDTO(HttpStatus.OK, accountDTO);
    }

    public RespDTO findAllAccounts() {
        logger.info("Starting process to find all accounts");

        List<Account> accounts = accountRepository.findAll();

        if (accounts.isEmpty()) {
            logger.error("No Accounts found");
            throw new EntityNotFoundException("No Accounts found");
        }
        logger.info("Number of accounts found: {}", accounts.size());

        SimpleAccountListWithUserDTO accountListDTO = SimpleAccountListWithUserDTO.fromAccounts(accounts);
        logger.info("Accounts retrieved successfully");
        return new RespDTO(HttpStatus.OK, accountListDTO);
    }

    public RespDTO findAccountById(UUID id) {
        logger.info("Starting process to find account by id: {}", id);

        Account existingAccount = accountRepository.findById(id);
        if (existingAccount == null) {
            logger.error("Account not found with id: {}", id);
            throw new EntityNotFoundException("Account not found");
        }

        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(existingAccount);
        logger.info("Account retrieved successfully: {}", accountDTO);
        return new RespDTO(HttpStatus.OK, accountDTO);
    }

    @Transactional
    public RespDTO updateAccount(UUID id, Account account) {
        logger.info("Starting account update process for id: {}", id);

        Account existingAccount = accountRepository.findById(id);
        if (existingAccount == null) {
            logger.error("Account not found with id: {}", id);
            throw new EntityNotFoundException("Account not found");
        }

        if (!existingAccount.isActive()) {
            logger.error("Attempt to update inactive account with id: {}", id);
            throw new IllegalArgumentException("This account is inactive");
        }

        logger.info("Validating fields for account update: {}", account);
        validate.validateUpdateAccount(account);

        existingAccount.setAccountType(account.getAccountType());
        existingAccount.setAgencyNumber(account.getAgencyNumber());
        existingAccount.setAccountNumber(account.getAccountNumber());

        logger.info("Saving updated account: {}", existingAccount);
        Account updatedAccount = accountRepository.save(existingAccount);
        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(updatedAccount);
        logger.info("Account updated successfully: {}", accountDTO);
        return new RespDTO(HttpStatus.OK, accountDTO);
    }

    @Transactional
    public RespDTO deleteAccount(UUID id) {
        logger.info("Starting account deletion process for id: {}", id);

        Account existingAccount = accountRepository.findById(id);
        if (existingAccount == null) {
            logger.error("Account not found with id: {}", id);
            throw new EntityNotFoundException("Account not found");
        }

        if (!existingAccount.isActive()) {
            logger.error("Attempt to delete already inactive account with id: {}", id);
            throw new IllegalArgumentException("This account is already inactive");
        }

        logger.info("Inactivating account: {}", existingAccount);
        existingAccount.setActive(false);
        existingAccount.setInactivatedAt(LocalDateTime.now());

        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(accountRepository.save(existingAccount));
        logger.info("Account inactivated successfully: {}", accountDTO);
        return new RespDTO(HttpStatus.OK, accountDTO);
    }
}