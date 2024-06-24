package com.cadastro.pix.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.dto.pixKey.CreatePixKeyDTO;
import com.cadastro.pix.dto.pixKey.PixKeyDTO;
import com.cadastro.pix.dto.pixKey.PixKeyListWithAccountAndUserDTO;
import com.cadastro.pix.dto.pixKey.PixKeyWithAccountDTO;
import com.cadastro.pix.utils.Validate;
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

    @Mock
    private Validate validate;


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

    private User validLegalUserActive() {
        User newUser = new User();
        newUser.setPersonType("juridica");
        newUser.setUserName("João");
        newUser.setUserLastName("Silva");
        newUser.setPhone("+5511998765432");
        newUser.setEmail("joao.silva@teste.com");
        newUser.setIdentification("06947283000160");
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return newUser;
    }

    private Account validIndividualAccount() {
        Account validAccount = new Account();
        validAccount.setId(UUID.randomUUID());
        validAccount.setAccountType("corrente");
        validAccount.setAgencyNumber(1234);
        validAccount.setAccountNumber(12345678);
        validAccount.setUser(validIndividualUserActive());
        validAccount.setActive(true);

        return validAccount;
    }

    private Account validLegalAccount() {
        Account validAccount = new Account();
        validAccount.setId(UUID.randomUUID());
        validAccount.setAccountType("corrente");
        validAccount.setAgencyNumber(1234);
        validAccount.setAccountNumber(87654321);
        validAccount.setUser(validLegalUserActive());
        validAccount.setActive(true);

        return validAccount;
    }

    private PixKey validPixKey() {
        PixKey validPixKey = new PixKey();
        validPixKey.setId(UUID.randomUUID());
        validPixKey.setKeyType("cpf");
        validPixKey.setKeyValue("48428781850");
        validPixKey.setAccount(validIndividualAccount());
        validPixKey.setActive(true);

        return validPixKey;
    }

    private CreatePixKeyDTO validCreatePixKeyDTO() {
        CreatePixKeyDTO validCreatePixKeyDTO = new CreatePixKeyDTO();
        validCreatePixKeyDTO.setKeyType("email");
        validCreatePixKeyDTO.setKeyValue("teste@teste.com");
        validCreatePixKeyDTO.setAgencyNumber(1234);
        validCreatePixKeyDTO.setAccountNumber(12345678);

        return validCreatePixKeyDTO;
    }

    private CreatePixKeyDTO validLegalCreatePixKeyDTO() {
        CreatePixKeyDTO validCreatePixKeyDTO = new CreatePixKeyDTO();
        validCreatePixKeyDTO.setKeyType("email");
        validCreatePixKeyDTO.setKeyValue("teste@teste.com");
        validCreatePixKeyDTO.setAgencyNumber(1234);
        validCreatePixKeyDTO.setAccountNumber(87654321);

        return validCreatePixKeyDTO;
    }

    @Test
    void testCreatePixKey_Success() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.save(any(PixKey.class))).thenAnswer(invocation -> {
            PixKey pixKey = invocation.getArgument(0);
            pixKey.setId(UUID.randomUUID());
            return pixKey;
        });

        RespDTO respDTO = pixKeyService.createPixKey(validCreatePixKeyDTO);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(PixKeyDTO.class, respDTO.getData());
    }

    @Test
    void testCreatePixKey_AccountNotFound() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            pixKeyService.createPixKey(validCreatePixKeyDTO);
        });

        assertEquals("There is no such account with this agency number and account", exception.getMessage());
    }

    @Test
    void testFindAll_Success() {
        List<PixKey> pixKeys = new ArrayList<>();
        pixKeys.add(validPixKey());
        pixKeys.add(validPixKey());

        when(pixKeyRepository.findAll()).thenReturn(pixKeys);

        RespDTO respDTO = pixKeyService.findAll();

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(PixKeyListWithAccountAndUserDTO.class, respDTO.getData());
        assertEquals(2, ((PixKeyListWithAccountAndUserDTO) respDTO.getData()).getPixKeys().size());
    }

    @Test
    void testFindAll_EmptyPixKeys() {
        List<PixKey> pixKeys = new ArrayList<>();

        when(pixKeyRepository.findAll()).thenReturn(pixKeys);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                pixKeyService.findAll());

        assertEquals("No Pix keys found", exception.getMessage());
    }

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        PixKey pixKey = validPixKey();
        pixKey.setId(id);

        when(pixKeyRepository.findById(id)).thenReturn(Optional.of(pixKey));

        RespDTO respDTO = pixKeyService.findById(id);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(PixKeyWithAccountDTO.class, respDTO.getData());
    }

    @Test
    void testFindById_NotFount() {
        UUID id = UUID.randomUUID();

        when(pixKeyRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                pixKeyService.findById(id));

        assertEquals("Pix key not found", exception.getMessage());
    }

    @Test
    void testFindByType_Success() {
        String keyType = "email";
        List<PixKey> pixKeys = new ArrayList<>();
        pixKeys.add(validPixKey());

        when(pixKeyRepository.findByKeyType(keyType)).thenReturn(pixKeys);

        RespDTO respDTO = pixKeyService.findByType(keyType);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(PixKeyListWithAccountAndUserDTO.class, respDTO.getData());
    }

    @Test
    void testFindByType_KeyTypeNotFound() {
        String keyType = "email";
        when(pixKeyRepository.findByKeyType(keyType)).thenReturn(new ArrayList<>());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                pixKeyService.findByType(keyType));

        assertEquals("No pix keys found for the specified type", exception.getMessage());
    }

    @Test
    void testFindByAgencyAndAccount_Success() {
        int agencyNumber = 1234;
        int accountNumber = 12345678;

        List<PixKey> pixKeys = new ArrayList<>();
        pixKeys.add(validPixKey());

        Account validAccount = validIndividualAccount();
        validAccount.setPixKeys(pixKeys);

        when(accountRepository.findByAgencyNumberAndAccountNumber(agencyNumber, accountNumber)).thenReturn(validAccount);

        RespDTO respDTO = pixKeyService.findByAgencyAndAccount(agencyNumber, accountNumber);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(PixKeyListWithAccountAndUserDTO.class, respDTO.getData());
    }

    @Test
    void testFindByAgencyAndAccount_AccountNotFound() {
        int agencyNumber = 1234;
        int accountNumber = 12345678;

        when(accountRepository.findByAgencyNumberAndAccountNumber(agencyNumber, accountNumber)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                pixKeyService.findByAgencyAndAccount(agencyNumber, accountNumber));

        assertEquals("There is no such account with this agency number and account", exception.getMessage());
    }

    @Test
    void testFindByAgencyAndAccount_EmptyPixKeyList() {
        int agencyNumber = 1234;
        int accountNumber = 12345678;

        Account account = validIndividualAccount();

        when(accountRepository.findByAgencyNumberAndAccountNumber(agencyNumber, accountNumber)).thenReturn(account);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                pixKeyService.findByAgencyAndAccount(agencyNumber, accountNumber));

        assertEquals("No pix keys found for the specified type", exception.getMessage());
    }

    @Test
    void testFindByUserName_Success() {
        String userName = "Joao";
        List<PixKey> pixKeys = new ArrayList<>();
        pixKeys.add(validPixKey());

        when(pixKeyRepository.findByUserName(userName)).thenReturn(pixKeys);

        RespDTO respDTO = pixKeyService.findByUserName(userName);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(PixKeyListWithAccountAndUserDTO.class, respDTO.getData());

        verify(pixKeyRepository, times(1)).findByUserName(userName);
    }

    @Test
    void testFindByUserName_UserNotFound() {
        String userName = "user@example.com";
        when(pixKeyRepository.findByUserName(userName)).thenReturn(new ArrayList<>());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                pixKeyService.findByUserName(userName));

        assertEquals("There is no user with that name", exception.getMessage());
    }

    @Test
    void testFindByCreatedAt_Success() {
        LocalDate date = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PixKey> pixKeys = new ArrayList<>();
        pixKeys.add(validPixKey());

        when(pixKeyRepository.findByCreatedAtBetween(startOfDay, endOfDay)).thenReturn(pixKeys);

        RespDTO respDTO = pixKeyService.findByCreatedAt(date);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(PixKeyListWithAccountAndUserDTO.class, respDTO.getData());

        verify(pixKeyRepository, times(1)).findByCreatedAtBetween(startOfDay, endOfDay);
    }

    @Test
    void testFindByCreatedAt_DateNull() {
        RespDTO respDTO = pixKeyService.findByCreatedAt(null);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.BAD_REQUEST, respDTO.getHttpStatus());
        assertEquals("The inclusion date must be provided for consultation", respDTO.getMessage());
    }

    @Test
    void testFindByCreatedAt_PixKeyListEmpty() {
        LocalDate date = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PixKey> pixKeys = new ArrayList<>();
//        pixKeys.add(validPixKey());

        when(pixKeyRepository.findByCreatedAtBetween(startOfDay, endOfDay)).thenReturn(pixKeys);


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                pixKeyService.findByCreatedAt(date));

        assertEquals("No Pix keys found on that date", exception.getMessage());
    }

    @Test
    void testFindByInactivatedAt_Success() {
        LocalDate date = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PixKey> pixKeys = new ArrayList<>();
        pixKeys.add(validPixKey());

        when(pixKeyRepository.findByInactivatedAtBetween(startOfDay, endOfDay)).thenReturn(pixKeys);

        RespDTO respDTO = pixKeyService.findByInactivatedAt(date);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(PixKeyListWithAccountAndUserDTO.class, respDTO.getData());
    }

    @Test
    void testFindByInactivatedAt_DateNull() {
        RespDTO respDTO = pixKeyService.findByInactivatedAt(null);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.BAD_REQUEST, respDTO.getHttpStatus());
        assertEquals("The inactivation date must be provided for consultation", respDTO.getMessage());
    }

    @Test
    void testFindByInactivatedAt_PixKeyListEmpty() {
        LocalDate date = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PixKey> pixKeys = new ArrayList<>();
//        pixKeys.add(validPixKey());

        when(pixKeyRepository.findByInactivatedAtBetween(startOfDay, endOfDay)).thenReturn(pixKeys);


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                pixKeyService.findByInactivatedAt(date));

        assertEquals("There is no pix key inactivated on this date", exception.getMessage());
    }

    //DELETE
    @Test
    void testDeletePixKey_Success() {
        UUID id = UUID.randomUUID();
        PixKey pixKey = validPixKey();
        pixKey.setId(id);

        when(pixKeyRepository.findById(id)).thenReturn(Optional.of(pixKey));

        RespDTO respDTO = pixKeyService.deletePixKey(id);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertFalse(((PixKeyDTO) respDTO.getData()).getActive());
        assertInstanceOf(PixKeyDTO.class, respDTO.getData());
    }

    @Test
    void testDeletePixKey_KeyNotFound() {
        UUID id = UUID.randomUUID();
        when(pixKeyRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                pixKeyService.deletePixKey(id));

        assertEquals("Pix key not found", exception.getMessage());
    }

    @Test
    void testDeletePixKey_KeyInactive() {
        UUID id = UUID.randomUUID();
        PixKey validPixKey = validPixKey();
        validPixKey.setActive(false);
        when(pixKeyRepository.findById(id)).thenReturn(Optional.of(validPixKey));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.deletePixKey(id));

        assertEquals("Pix key is already inactive", exception.getMessage());
    }
}