package com.cadastro.pix.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cadastro.pix.domain.RespDTO;
import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.dto.account.CreateAccountDTO;
import com.cadastro.pix.dto.account.SimpleAccountListWithUserDTO;
import com.cadastro.pix.dto.account.SimpleAccountWithUserDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

    private CreateAccountDTO validCreateAccountDTO() {
        CreateAccountDTO validCreateAccountDTO = new CreateAccountDTO();
        validCreateAccountDTO.setIdentification("48428781850");
        validCreateAccountDTO.setAccountType("corrente");
        validCreateAccountDTO.setAgencyNumber(1234);
        validCreateAccountDTO.setAccountNumber(56789012);

        return validCreateAccountDTO;
    }

    //CREATE
    @Test
    void testCreateAccount_Success() {
        CreateAccountDTO validCreateAccountDTO = validCreateAccountDTO();
        User validUser = validIndividualUserActive();
        Account validAccount = validAccount();

        when(userRepository.findByIdentification(any(String.class))).thenReturn(validUser);
        when(accountRepository.save(any(Account.class))).thenReturn(validAccount);

        RespDTO respDTO = accountService.createAccount(validCreateAccountDTO);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertEquals(validAccount.getId(), ((SimpleAccountWithUserDTO) respDTO.getData()).getId());
        assertInstanceOf(SimpleAccountWithUserDTO.class, respDTO.getData());
    }

    @Test
    void testCreateAccount_UserNotFound() {
        CreateAccountDTO validCreateAccountDTO = validCreateAccountDTO();

        when(userRepository.findByIdentification(any(String.class))).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            accountService.createAccount(validCreateAccountDTO);
        });

        assertEquals("User not found with this identification", exception.getMessage());
    }

    @Test
    void testCreateAccount_ExistingAccount() {
        CreateAccountDTO validCreateAccountDTO = validCreateAccountDTO();
        User validUser = validIndividualUserActive();
        Account validAccount = validAccount();

        when(userRepository.findByIdentification(any(String.class))).thenReturn(validUser);
        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class))).thenReturn(validAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(validCreateAccountDTO);
        });

        assertEquals("There is already an account with that account number at this agency", exception.getMessage());
    }

    @Test
    void testCreateAccount_ExistingAccountInactivated() {
        CreateAccountDTO validCreateAccountDTO = validCreateAccountDTO();
        User validUser = validIndividualUserActive();
        Account validAccount = validAccount();
        validAccount.setActive(false);

        when(userRepository.findByIdentification(any(String.class))).thenReturn(validUser);
        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class))).thenReturn(validAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(validCreateAccountDTO);
        });

        assertEquals("There is already an inactive account with that account number at this agency", exception.getMessage());
    }

    @Test
    void testCreateAccount_InvalidAccountType() {
        CreateAccountDTO validCreateAccountDTO = validCreateAccountDTO();
        User validUser = validIndividualUserActive();

        validCreateAccountDTO.setAccountType("invalid");

        when(userRepository.findByIdentification(any(String.class))).thenReturn(validUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(validCreateAccountDTO);
        });

        assertEquals("Invalid account type", exception.getMessage());
    }

    @Test
    void testCreateAccount_NullAgencyNumber() {
        CreateAccountDTO validCreateAccountDTO = validCreateAccountDTO();
        User validUser = validIndividualUserActive();

        validCreateAccountDTO.setAgencyNumber(null);

        when(userRepository.findByIdentification(any(String.class))).thenReturn(validUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(validCreateAccountDTO);
        });

        assertEquals("Invalid agency number", exception.getMessage());
    }

    @Test
    void testCreateAccount_BiggerAgencyNumber() {
        CreateAccountDTO validCreateAccountDTO = validCreateAccountDTO();
        User validUser = validIndividualUserActive();

        validCreateAccountDTO.setAgencyNumber(12345);

        when(userRepository.findByIdentification(any(String.class))).thenReturn(validUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(validCreateAccountDTO);
        });

        assertEquals("Invalid agency number", exception.getMessage());
    }

    @Test
    void testCreateAccount_NullAccountNumber() {
        CreateAccountDTO validCreateAccountDTO = validCreateAccountDTO();
        User validUser = validIndividualUserActive();

        validCreateAccountDTO.setAccountNumber(null);

        when(userRepository.findByIdentification(any(String.class))).thenReturn(validUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(validCreateAccountDTO);
        });

        assertEquals("Invalid account number", exception.getMessage());
    }

    @Test
    void testCreateAccount_BiggerAccountNumber() {
        CreateAccountDTO validCreateAccountDTO = validCreateAccountDTO();
        User validUser = validIndividualUserActive();

        validCreateAccountDTO.setAccountNumber(123456789);

        when(userRepository.findByIdentification(any(String.class))).thenReturn(validUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(validCreateAccountDTO);
        });

        assertEquals("Invalid account number", exception.getMessage());
    }

    //UPDATE
    @Test
    void testUpdateAccount_Success() {
        Account validAccount = validAccount();
        validAccount.setAccountNumber(87654321);

        UUID id = UUID.randomUUID();
        Account existingAccount = validAccount();
        existingAccount.setId(id);

        when(accountRepository.findById(any(UUID.class))).thenReturn(existingAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        RespDTO respDTO = accountService.updateAccount(id, validAccount);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertEquals(id, ((SimpleAccountWithUserDTO) respDTO.getData()).getId());
        assertEquals(validAccount.getAccountNumber(), ((SimpleAccountWithUserDTO) respDTO.getData()).getAccountNumber());
        assertEquals(validAccount.getAgencyNumber(), ((SimpleAccountWithUserDTO) respDTO.getData()).getAgencyNumber());
        assertInstanceOf(SimpleAccountWithUserDTO.class, respDTO.getData());
    }

    @Test
    void testUpdateAccount_AccountNotFound() {
        Account validAccount = validAccount();

        UUID id = UUID.randomUUID();
        validAccount.setId(id);
        when(accountRepository.findById(any(UUID.class))).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            accountService.updateAccount(id, validAccount);
        });

        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void testUpdateAccount_InactiveAccount() {
        Account validAccount = validAccount();

        UUID id = UUID.randomUUID();
        Account existingAccount = validAccount();
        existingAccount.setId(id);
        existingAccount.setActive(false);

        when(accountRepository.findById(id)).thenReturn(existingAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.updateAccount(id, validAccount);
        });

        assertEquals("This account is inactive", exception.getMessage());
    }

    @Test
    void testUpdateAccount_InvalidAccountType() {
        Account validAccount = validAccount();

        validAccount.setAccountType("invalid");

        UUID id = validAccount.getId();
        when(accountRepository.findById(id)).thenReturn(validAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.updateAccount(id, validAccount);
        });

        assertEquals("Invalid account type", exception.getMessage());
    }

    @Test
    void testUpdateAccount_NullAgencyNumber() {
        Account validAccount = validAccount();

        validAccount.setAgencyNumber(null);

        UUID id = validAccount.getId();
        when(accountRepository.findById(id)).thenReturn(validAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.updateAccount(id, validAccount);
        });

        assertEquals("Invalid agency number", exception.getMessage());
    }

    @Test
    void testUpdateAccount_BiggerAgencyNumber() {
        Account validAccount = validAccount();

        validAccount.setAgencyNumber(12345);

        UUID id = validAccount.getId();
        when(accountRepository.findById(id)).thenReturn(validAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.updateAccount(id, validAccount);
        });

        assertEquals("Invalid agency number", exception.getMessage());
    }

    @Test
    void testUpdateAccount_NullAccountNumber() {
        Account validAccount = validAccount();

        validAccount.setAgencyNumber(null);

        UUID id = validAccount.getId();
        when(accountRepository.findById(id)).thenReturn(validAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.updateAccount(id, validAccount);
        });

        assertEquals("Invalid agency number", exception.getMessage());
    }

    @Test
    void testUpdateAccount_BiggerAccountNumber() {
        Account validAccount = validAccount();

        validAccount.setAgencyNumber(123456789);

        UUID id = validAccount.getId();
        when(accountRepository.findById(id)).thenReturn(validAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.updateAccount(id, validAccount);
        });

        assertEquals("Invalid agency number", exception.getMessage());
    }

    //DELETE
    @Test
    void testDeleteAccount_Success() {
        UUID id = UUID.randomUUID();
        Account existingAccount = validAccount();
        existingAccount.setId(id);

        when(accountRepository.findById(id)).thenReturn(existingAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        RespDTO respDTO = accountService.deleteAccount(id);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(SimpleAccountWithUserDTO.class, respDTO.getData());
        assertEquals(id, ((SimpleAccountWithUserDTO) respDTO.getData()).getId());
    }

    @Test
    void testDeleteAccount_AccountNotFound() {
        UUID id = UUID.randomUUID();

        when(accountRepository.findById(id)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            accountService.deleteAccount(id);
        });

        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void testDeleteAccount_InactiveAccount() {
        UUID id = UUID.randomUUID();
        Account existingAccount = validAccount();
        existingAccount.setId(id);
        existingAccount.setActive(false);

        when(accountRepository.findById(id)).thenReturn(existingAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.deleteAccount(id);
        });

        assertEquals("This account is already inactive", exception.getMessage());
    }

    //GET
    @Test
    void testFindAllAccounts_Success() {
        List<Account> accounts = Arrays.asList(validAccount(), validAccount());
        when(accountRepository.findAll()).thenReturn(accounts);

        RespDTO respDTO = accountService.findAllAccounts();

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(SimpleAccountListWithUserDTO.class, respDTO.getData());
        assertEquals(2, ((SimpleAccountListWithUserDTO) respDTO.getData()).getAccounts().size());
    }

    @Test
    void testFindAllAccounts_NotFound() {
        List<Account> accounts = new ArrayList<>();

        when(accountRepository.findAll()).thenReturn(accounts);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                accountService.findAllAccounts());

        assertEquals("No Accounts found", exception.getMessage());

    }

    @Test
    void testFindAccountById_Success() {
        UUID id = UUID.randomUUID();
        Account existingAccount = validAccount();
        existingAccount.setId(id);

        when(accountRepository.findById(id)).thenReturn(existingAccount);

        RespDTO respDTO = accountService.findAccountById(id);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(SimpleAccountWithUserDTO.class, respDTO.getData());
        assertEquals(id, ((SimpleAccountWithUserDTO) respDTO.getData()).getId());
    }

    @Test
    void testFindAccountById_AccountNotFound() {
        UUID id = UUID.randomUUID();

        when(accountRepository.findById(id)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            accountService.findAccountById(id);
        });

        assertEquals("Account not found", exception.getMessage());
    }
}

