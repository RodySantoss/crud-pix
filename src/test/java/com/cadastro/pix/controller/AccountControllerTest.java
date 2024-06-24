package com.cadastro.pix.controller;

import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.account.CreateAccountDTO;
import com.cadastro.pix.dto.account.SimpleAccountListWithUserDTO;
import com.cadastro.pix.dto.account.SimpleAccountWithUserDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CreateAccountDTO validCreateAccountDTO() {
        CreateAccountDTO validCreateAccountDTO = new CreateAccountDTO();
        validCreateAccountDTO.setIdentification("48428781850");
        validCreateAccountDTO.setAccountType("corrente");
        validCreateAccountDTO.setAgencyNumber(1234);
        validCreateAccountDTO.setAccountNumber(56789012);

        return validCreateAccountDTO;
    }

    private User validIndividualUserActive() {
        User newUser = new User();
        newUser.setPersonType("fisica");
        newUser.setUserName("João");
        newUser.setUserLastName("Silva");
        newUser.setPhone("+5511998765432");
        newUser.setEmail("joao.silva@teste.com");
        newUser.setIdentification("48428781850");
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return newUser;
    }

    private Account validAccount() {
        Account validAccount = new Account();
        validAccount.setId(UUID.randomUUID());
        validAccount.setAccountType("corrente");
        validAccount.setAgencyNumber(1234);
        validAccount.setAccountNumber(56789012);
        validAccount.setUser(validIndividualUserActive());
        validAccount.setActive(true);

        return validAccount;
    }

    @Test
    void testCreateAccount_Success() {
        Account account = validAccount();

        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(account.getId());

        RespDTO expectedResponse = new RespDTO(HttpStatus.OK, accountDTO);
        when(accountService.createAccount(any(CreateAccountDTO.class))).thenReturn(expectedResponse);

        ResponseEntity<RespDTO> response = accountController.createAccount(validCreateAccountDTO());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(accountDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testCreateAccount_EntityNotFound() {
        when(accountService.createAccount(any(CreateAccountDTO.class)))
                .thenThrow(new EntityNotFoundException("User not found with this identification"));

        ResponseEntity<RespDTO> response = accountController.createAccount(validCreateAccountDTO());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(
                "User not found with this identification",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testCreateAccount_IllegalArgumentException() {
        when(accountService.createAccount(any(CreateAccountDTO.class)))
                .thenThrow(new IllegalArgumentException("There is already an account with that account number at this agency"));

        ResponseEntity<RespDTO> response = accountController.createAccount(validCreateAccountDTO());

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(
                "There is already an account with that account number at this agency",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testFindAllAccounts_Success() {
        List<Account> accounts = Arrays.asList(validAccount(), validAccount());
        SimpleAccountListWithUserDTO accountListDTO = SimpleAccountListWithUserDTO.fromAccounts(accounts);

        RespDTO expectedResponse = new RespDTO(HttpStatus.OK, accountListDTO);
        when(accountService.findAllAccounts()).thenReturn(expectedResponse);

        ResponseEntity<RespDTO> response = accountController.findAllAccounts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(accountListDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testFindAllAccounts_EntityNotFound() {
        when(accountService.findAllAccounts()).thenThrow(new EntityNotFoundException("No Accounts found"));

        ResponseEntity<RespDTO> response = accountController.findAllAccounts();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No Accounts found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testFindAccountById_Success() {
        UUID id = UUID.randomUUID();
        Account account = validAccount();
        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(account);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, accountDTO);
        when(accountService.findAccountById(any(UUID.class))).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = accountController.findAccountById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(accountDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testFindAccountById_EntityNotFound() {
        UUID id = UUID.randomUUID();
        when(accountService.findAccountById(id)).thenThrow(new EntityNotFoundException("Account not found"));

        ResponseEntity<RespDTO> response = accountController.findAccountById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testUpdateAccount_Success() {
        UUID id = UUID.randomUUID();
        Account account = validAccount();
        account.setId(id);

        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(account);

        RespDTO respDTO = new RespDTO(HttpStatus.OK, accountDTO);
        when(accountService.updateAccount(any(UUID.class), any(Account.class))).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = accountController.updateAccount(id, account);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(accountDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testUpdateAccount_EntityNotFound() {
        UUID id = UUID.randomUUID();
        Account user = validAccount();
        when(accountService.updateAccount(eq(id), any(Account.class))).thenThrow(new EntityNotFoundException("Account not found"));

        ResponseEntity<RespDTO> response = accountController.updateAccount(id, user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testUpdateAccount_IllegalArgumentException() {
        UUID id = UUID.randomUUID();
        Account account = validAccount();

        when(accountService.updateAccount(eq(id), any(Account.class)))
                .thenThrow(new IllegalArgumentException("Invalid account type"));

        ResponseEntity<RespDTO> response = accountController.updateAccount(id, account);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(
                "Invalid account type",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testDeleteUser_Success() {
        UUID id = UUID.randomUUID();
        Account account = validAccount();

        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(account);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, accountDTO);

        when(accountService.deleteAccount(any(UUID.class))).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = accountController.deleteAccount(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(accountDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testDeleteUser_EntityNotFound() {
        UUID id = UUID.randomUUID();

        when(accountService.deleteAccount(any(UUID.class))).thenThrow(new EntityNotFoundException("Account not found"));

        ResponseEntity<RespDTO> response = accountController.deleteAccount(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testDeleteUser_IllegalArgumentException() {
        UUID id = UUID.randomUUID();

        when(accountService.deleteAccount(any(UUID.class)))
                .thenThrow(new IllegalArgumentException("This account is already inactive"));

        ResponseEntity<RespDTO> response = accountController.deleteAccount(id);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(
                "This account is already inactive",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }
}
