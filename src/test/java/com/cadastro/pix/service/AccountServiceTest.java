package com.cadastro.pix.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cadastro.pix.domain.ReqObj;
import com.cadastro.pix.domain.RespDTO;
import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.account.dto.SimpleAccountListWithUserDTO;
import com.cadastro.pix.domain.account.dto.SimpleAccountWithUserDTO;
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

    private User validPhysicalUserActive() {
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
        validAccount.setUser(validPhysicalUserActive());
        validAccount.setActive(true);

        return validAccount;
    }

    private ReqObj validReqObj() {
        ReqObj validReqObj = new ReqObj();
        validReqObj.setIdentification("48428781850");
        validReqObj.setAccountType("corrente");
        validReqObj.setAgencyNumber(1234);
        validReqObj.setAccountNumber(56789012);

        return validReqObj;
    }

    @Test
    void testCreateAccount_Success() {
        ReqObj validReqObj = validReqObj();
        User validUser = validPhysicalUserActive();
        Account validAccount = validAccount();

        when(userRepository.findByIdentification(validReqObj.getIdentification())).thenReturn(validUser);
        when(accountRepository.save(any(Account.class))).thenReturn(validAccount);

        RespDTO response = accountService.createAccount(validReqObj);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertTrue(response.getData() instanceof SimpleAccountWithUserDTO);
        assertEquals(validAccount.getId(), ((SimpleAccountWithUserDTO) response.getData()).getId());
        verify(userRepository, times(1)).findByIdentification(validReqObj.getIdentification());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testCreateAccount_UserNotFound() {
        ReqObj validReqObj = validReqObj();

        when(userRepository.findByIdentification(validReqObj.getIdentification())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            accountService.createAccount(validReqObj);
        });

        assertEquals("Nao existe um user com essa identificaçao", exception.getMessage());
        verify(userRepository, times(1)).findByIdentification(validReqObj.getIdentification());
        verify(accountRepository, times(0)).save(any(Account.class));
    }

    @Test
    void testCreateAccount_InvalidAccountType() {
        ReqObj validReqObj = validReqObj();
        User validUser = validPhysicalUserActive();

        validReqObj.setAccountType("invalid");

        when(userRepository.findByIdentification(validReqObj.getIdentification())).thenReturn(validUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(validReqObj);
        });

        assertEquals("Tipo de conta inválido", exception.getMessage());
        verify(userRepository, times(1)).findByIdentification(validReqObj.getIdentification());
        verify(accountRepository, times(0)).save(any(Account.class));
    }

    @Test
    void testCreateAccount_InvalidAgencyNumber() {
        ReqObj validReqObj = validReqObj();
        User validUser = validPhysicalUserActive();

        validReqObj.setAgencyNumber(12345);

        when(userRepository.findByIdentification(validReqObj.getIdentification())).thenReturn(validUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(validReqObj);
        });

        assertEquals("Número da agência inválido", exception.getMessage());
        verify(userRepository, times(1)).findByIdentification(validReqObj.getIdentification());
        verify(accountRepository, times(0)).save(any(Account.class));
    }

    @Test
    void testCreateAccount_InvalidAccountNumber() {
        ReqObj validReqObj = validReqObj();
        User validUser = validPhysicalUserActive();
        Account validAccount = validAccount();

        validReqObj.setAccountNumber(123456789);

        when(userRepository.findByIdentification(validReqObj.getIdentification())).thenReturn(validUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(validReqObj);
        });

        assertEquals("Número da conta inválido", exception.getMessage());
        verify(userRepository, times(1)).findByIdentification(validReqObj.getIdentification());
        verify(accountRepository, times(0)).save(any(Account.class));
    }

    @Test
    void testCreateAccount_AccountAlreadyExists() {
        ReqObj validReqObj = validReqObj();
        User validUser = validPhysicalUserActive();
        Account validAccount = validAccount();

        when(userRepository.findByIdentification(validReqObj.getIdentification())).thenReturn(validUser);
        when(accountRepository.findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber())).thenReturn(validAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(validReqObj);
        });

        assertEquals("Ja existe uma conta com esse numero de conta nessa agencia", exception.getMessage());
        verify(userRepository, times(1)).findByIdentification(validReqObj.getIdentification());
        verify(accountRepository, times(1)).findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber());
        verify(accountRepository, times(0)).save(any(Account.class));
    }

    //UPDATE
    @Test
    void testUpdateAccount_Success() {
        Account validAccount = validAccount();
        validAccount.setAccountNumber(87654321);

        UUID accountId = UUID.randomUUID();
        Account existingAccount = validAccount();
        existingAccount.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(existingAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        RespDTO response = accountService.updateAccount(accountId.toString(), validAccount);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertTrue(response.getData() instanceof SimpleAccountWithUserDTO);
        assertEquals(accountId, ((SimpleAccountWithUserDTO) response.getData()).getId());
        assertEquals(validAccount.getAccountNumber(), ((SimpleAccountWithUserDTO) response.getData()).getAccountNumber());
        assertEquals(validAccount.getAgencyNumber(), ((SimpleAccountWithUserDTO) response.getData()).getAgencyNumber());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testUpdateAccount_AccountNotFound() {
        Account validAccount = validAccount();

        UUID accountId = UUID.randomUUID();
        validAccount.setId(accountId);
        when(accountRepository.findById(accountId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            accountService.updateAccount(accountId.toString(), validAccount);
        });

        assertEquals("Conta nao encontrada", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(0)).save(any(Account.class));
    }

    @Test
    void testUpdateAccount_InactiveAccount() {
        Account validAccount = validAccount();

        UUID accountId = UUID.randomUUID();
        Account existingAccount = validAccount;
        existingAccount.setId(accountId);
        existingAccount.setActive(false);

        when(accountRepository.findById(accountId)).thenReturn(existingAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.updateAccount(accountId.toString(), validAccount);
        });

        assertEquals("Conta inativa", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(0)).save(any(Account.class));
    }

    @Test
    void testUpdateAccount_InvalidAccountType() {
        Account validAccount = validAccount();

        validAccount.setAccountType("invalid");

        UUID accountId = validAccount.getId();
        when(accountRepository.findById(accountId)).thenReturn(validAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.updateAccount(accountId.toString(), validAccount);
        });

        assertEquals("Tipo de conta inválido", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(0)).save(any(Account.class));
    }

    //DELETE
    @Test
    void testDeleteAccount_Success() {
        UUID accountId = UUID.randomUUID();
        Account existingAccount = validAccount();
        existingAccount.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(existingAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        RespDTO response = accountService.deleteAccount(accountId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertTrue(response.getData() instanceof SimpleAccountWithUserDTO);
        assertEquals(accountId, ((SimpleAccountWithUserDTO) response.getData()).getId());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testDeleteAccount_AccountNotFound() {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            accountService.deleteAccount(accountId);
        });

        assertEquals("Conta nao encontrada", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(0)).save(any(Account.class));
    }

    @Test
    void testDeleteAccount_InactiveAccount() {
        UUID accountId = UUID.randomUUID();
        Account existingAccount = validAccount();
        existingAccount.setId(accountId);
        existingAccount.setActive(false);

        when(accountRepository.findById(accountId)).thenReturn(existingAccount);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.deleteAccount(accountId);
        });

        assertEquals("Conta ja esta inativa", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(0)).save(any(Account.class));
    }

    @Test
    void testFindAllAccounts_Success() {
        List<Account> accounts = Arrays.asList(validAccount(), validAccount());
        when(accountRepository.findAll()).thenReturn(accounts);

        RespDTO response = accountService.findAllAccounts();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertTrue(response.getData() instanceof SimpleAccountListWithUserDTO);
        assertEquals(2, ((SimpleAccountListWithUserDTO) response.getData()).getAccounts().size());
        verify(accountRepository, times(1)).findAll();
    }

//    @Test
//    void testFindAccountById_Success() {
//        UUID accountId = UUID.randomUUID();
//        Account existingAccount = validAccount();
//        existingAccount.setId(accountId);
//
//        when(accountRepository.findById(accountId)).thenReturn(existingAccount);
//
//        RespDTO response = accountService.findAccountById(accountId);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getHttpStatus());
//        assertTrue(response.getData() instanceof SimpleAccountWithUserDTO);
//        assertEquals(accountId, ((SimpleAccountWithUserDTO) response.getData()).getId());
//        verify(accountRepository, times(1)).findById(accountId);
//    }

    @Test
    void testFindAccountById_AccountNotFound() {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            accountService.findAccountById(accountId);
        });

        assertEquals("Conta nao encontrada", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
    }
}

