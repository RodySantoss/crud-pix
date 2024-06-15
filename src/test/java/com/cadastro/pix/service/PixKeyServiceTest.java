package com.cadastro.pix.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.cadastro.pix.domain.ReqObj;
import com.cadastro.pix.domain.RespDTO;
import com.cadastro.pix.domain.pixKey.dto.PixKeyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.PixKeyRepository;
import com.cadastro.pix.repository.UserRepository;
import com.cadastro.pix.service.PixKeyService;

@ExtendWith(MockitoExtension.class)
public class PixKeyServiceTest {

    @Mock
    private PixKeyRepository pixKeyRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PixKeyService pixKeyService;

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

    private PixKey validPixKey() {
        PixKey validPixKey = new PixKey();
        validPixKey.setId(UUID.randomUUID());
        validPixKey.setKeyType("cpf");
        validPixKey.setKeyValue("48428781850");
        validPixKey.setAccount(validAccount());
        validPixKey.setActive(true);

        return validPixKey;
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
    void testCreatePixKey_Success() {
        ReqObj validReqObj = validReqObj();
        User validUser = validPhysicalUserActive();
        Account validAccount = validAccount();

        when(accountRepository.findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValue(validReqObj.getKeyValue())).thenReturn(false);
        when(pixKeyRepository.save(any(PixKey.class))).thenAnswer(invocation -> {
            PixKey pixKey = invocation.getArgument(0);
            pixKey.setId(UUID.randomUUID());
            return pixKey;
        });

        RespDTO response = pixKeyService.createPixKey(validReqObj);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertTrue(response.getData() instanceof PixKeyDTO);
        verify(accountRepository, times(1)).findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber());
        verify(pixKeyRepository, times(1)).existsByKeyValue(validReqObj.getKeyValue());
        verify(pixKeyRepository, times(1)).save(any(PixKey.class));
    }

    @Test
    void testCreatePixKey_AccountNotFound() {
        ReqObj validReqObj = validReqObj();

        when(accountRepository.findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            pixKeyService.createPixKey(validReqObj);
        });

        assertEquals("Nao existe uma conta com essa com esse numero agencia e conta", exception.getMessage());
        verify(accountRepository, times(1)).findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber());
        verify(pixKeyRepository, times(0)).existsByKeyValue(anyString());
        verify(pixKeyRepository, times(0)).save(any(PixKey.class));
    }

    @Test
    void testCreatePixKey_DuplicateKeyValue() {
        ReqObj validReqObj = validReqObj();
        Account validAccount = validAccount();

        when(accountRepository.findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValue(validReqObj.getKeyValue())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pixKeyService.createPixKey(validReqObj);
        });

        assertEquals("Valor de chave já cadastrado", exception.getMessage());
        verify(accountRepository, times(1)).findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber());
        verify(pixKeyRepository, times(1)).existsByKeyValue(validReqObj.getKeyValue());
        verify(pixKeyRepository, times(0)).save(any(PixKey.class));
    }

    @Test
    void testCreatePixKey_PhysicalPersonLimitExceeded() {
        ReqObj validReqObj = validReqObj();
        Account validAccount = validAccount();

        List<PixKey> pixKeyList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PixKey pixKey = validPixKey();
            pixKeyList.add(pixKey);
        }
        validAccount.setPixKeys(pixKeyList);

        when(accountRepository.findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValue(validReqObj.getKeyValue())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pixKeyService.createPixKey(validReqObj);
        });

        assertEquals("Limite de 5 chaves por conta para Pessoa Física excedido", exception.getMessage());
        verify(accountRepository, times(1)).findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber());
        verify(pixKeyRepository, times(0)).existsByKeyValue(anyString());
        verify(pixKeyRepository, times(0)).save(any(PixKey.class));
    }

    @Test
    void testCreatePixKey_LegalPersonLimitExceeded() {
        ReqObj validReqObj = validReqObj();
        User validUser = validPhysicalUserActive();
        Account validAccount = validAccount();

        List<PixKey> pixKeyList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PixKey pixKey = new PixKey();
            pixKeyList.add(pixKey);
        }
        validAccount.setPixKeys(pixKeyList);

        when(accountRepository.findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValue(validReqObj.getKeyValue())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pixKeyService.createPixKey(validReqObj);
        });

        assertEquals("Limite de 20 chaves por conta para Pessoa Jurídica excedido", exception.getMessage());
        verify(accountRepository, times(1)).findByAgencyNumberAndAccountNumber(validReqObj.getAgencyNumber(), validReqObj.getAccountNumber());
        verify(pixKeyRepository, times(0)).existsByKeyValue(anyString());
        verify(pixKeyRepository, times(0)).save(any(PixKey.class));
    }
}