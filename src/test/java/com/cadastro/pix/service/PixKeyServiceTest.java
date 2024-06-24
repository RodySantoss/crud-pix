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
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);
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
    void testValidateCreatePixKey_KeyValueExists() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Pix key value already registered", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_PhoneFormatWithouPlusSignal() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();
        validCreatePixKeyDTO.setKeyType("celular");
        validCreatePixKeyDTO.setKeyValue("5511976110609");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_PhoneNumberSmallerThenExpected() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();
        validCreatePixKeyDTO.setKeyType("celular");
        validCreatePixKeyDTO.setKeyValue("+5511998765");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_PhoneNumberBiggerThenExpected() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();
        validCreatePixKeyDTO.setKeyType("celular");
        validCreatePixKeyDTO.setKeyValue("+551198765432109");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_EmailWithoutArroba() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();
        validCreatePixKeyDTO.setKeyType("email");
        validCreatePixKeyDTO.setKeyValue("teste.teste.com");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_EmailWithoutDotPlusText() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();
        validCreatePixKeyDTO.setKeyType("email");
        validCreatePixKeyDTO.setKeyValue("teste@teste");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CannotRegisterCNPJKey() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();
        validCreatePixKeyDTO.setKeyType("cnpj");
        validCreatePixKeyDTO.setKeyValue("06947283000160");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Individuals cannot register a CNPJ key", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CPFDifferentThenUserCPF() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();
        validCreatePixKeyDTO.setKeyType("cpf");
        validCreatePixKeyDTO.setKeyValue("36216995898");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("The CPF key must be the same as the account's CPF", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CPFKeyAlreadyExist() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();
        validCreatePixKeyDTO.setKeyType("cpf");
        validCreatePixKeyDTO.setKeyValue("48428781850");

        PixKey pixKey = validPixKey();
        List<PixKey> pixKeyList = new ArrayList<>();

        pixKeyList.add(pixKey);

        validAccount.setPixKeys(pixKeyList);

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("CPF key already registered for this account", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CPFKeyAlphanumeric() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();
        validCreatePixKeyDTO.setKeyType("cpf");
        validCreatePixKeyDTO.setKeyValue("484287818as");
        validAccount.getUser().setIdentification("484287818as");

        List<PixKey> pixKeyList = new ArrayList<>();

        validAccount.setPixKeys(pixKeyList);

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CPFKeySmaller() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();
        validCreatePixKeyDTO.setKeyType("cpf");
        validCreatePixKeyDTO.setKeyValue("4842878185");
        validAccount.getUser().setIdentification("4842878185");

        List<PixKey> pixKeyList = new ArrayList<>();

        validAccount.setPixKeys(pixKeyList);

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CPFKeyBigger() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();
        validCreatePixKeyDTO.setKeyType("cpf");
        validCreatePixKeyDTO.setKeyValue("484287818501");
        validAccount.getUser().setIdentification("484287818501");

        List<PixKey> pixKeyList = new ArrayList<>();

        validAccount.setPixKeys(pixKeyList);

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CannotRegisterCPFKey() {
        CreatePixKeyDTO validCreatePixKeyDTO = validLegalCreatePixKeyDTO();
        Account validAccount = validLegalAccount();
        validCreatePixKeyDTO.setKeyType("cpf");
        validCreatePixKeyDTO.setKeyValue("36216995898");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Legal entities cannot register a CPF key", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CNPJDifferentThenUser() {
        CreatePixKeyDTO validCreatePixKeyDTO = validLegalCreatePixKeyDTO();
        Account validAccount = validLegalAccount();
        validCreatePixKeyDTO.setKeyType("cnpj");
        validCreatePixKeyDTO.setKeyValue("09188942000110");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("The CNPJ key must be the same as the account's CNPJ", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CNPJKeyAlreadyExist() {
        CreatePixKeyDTO validCreatePixKeyDTO = validLegalCreatePixKeyDTO();
        Account validAccount = validLegalAccount();
        validCreatePixKeyDTO.setKeyType("cnpj");
        validCreatePixKeyDTO.setKeyValue("06947283000160");

        PixKey pixKey = validPixKey();
        pixKey.setKeyType("cnpj");
        pixKey.setKeyValue("06947283000160");
        List<PixKey> pixKeyList = new ArrayList<>();

        pixKeyList.add(pixKey);

        validAccount.setPixKeys(pixKeyList);

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("CNPJ key already registered for this account", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CNPJKeyAlphanumeric() {
        CreatePixKeyDTO validCreatePixKeyDTO = validLegalCreatePixKeyDTO();
        Account validAccount = validLegalAccount();
        validCreatePixKeyDTO.setKeyType("cnpj");
        validCreatePixKeyDTO.setKeyValue("069472830001as");
        validAccount.getUser().setIdentification("069472830001as");

        List<PixKey> pixKeyList = new ArrayList<>();

        validAccount.setPixKeys(pixKeyList);

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CNPJKeySmaller() {
        CreatePixKeyDTO validCreatePixKeyDTO = validLegalCreatePixKeyDTO();
        Account validAccount = validLegalAccount();
        validCreatePixKeyDTO.setKeyType("cnpj");
        validCreatePixKeyDTO.setKeyValue("0694728300016");
        validAccount.getUser().setIdentification("0694728300016");

        List<PixKey> pixKeyList = new ArrayList<>();

        validAccount.setPixKeys(pixKeyList);

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CNPJKeyBigger() {
        CreatePixKeyDTO validCreatePixKeyDTO = validLegalCreatePixKeyDTO();
        Account validAccount = validLegalAccount();
        validCreatePixKeyDTO.setKeyType("cnpj");
        validCreatePixKeyDTO.setKeyValue("069472830001601");
        validAccount.getUser().setIdentification("069472830001601");

        List<PixKey> pixKeyList = new ArrayList<>();

        validAccount.setPixKeys(pixKeyList);

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_InvalidRandomKey() {
        CreatePixKeyDTO validCreatePixKeyDTO = validLegalCreatePixKeyDTO();
        Account validAccount = validLegalAccount();

        validCreatePixKeyDTO.setKeyType("aleatorio");
        validCreatePixKeyDTO.setKeyValue("550e8400-e29b-41d4-a716-44665544000G");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid random key", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_RandomKeySmallerThen36Char() {
        CreatePixKeyDTO validCreatePixKeyDTO = validLegalCreatePixKeyDTO();
        Account validAccount = validLegalAccount();

        validCreatePixKeyDTO.setKeyType("aleatorio");
        validCreatePixKeyDTO.setKeyValue("550e8400-e29b-41d4-a716-44665544000");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid random key", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_RandomKeyBiggerThen36Char() {
        CreatePixKeyDTO validCreatePixKeyDTO = validLegalCreatePixKeyDTO();
        Account validAccount = validLegalAccount();

        validCreatePixKeyDTO.setKeyType("aleatorio");
        validCreatePixKeyDTO.setKeyValue("550e8400-e29b-41d4-a716-4466554400001");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid random key", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_InvalidKeyType() {
        CreatePixKeyDTO validCreatePixKeyDTO = validLegalCreatePixKeyDTO();
        Account validAccount = validLegalAccount();

        validCreatePixKeyDTO.setKeyType("invalid");

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pixKeyService.createPixKey(validCreatePixKeyDTO));

        assertEquals("Invalid key type", exception.getMessage());
    }

    @Test
    void testCreatePixKey_IndividualPersonLimitExceeded() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validAccount = validIndividualAccount();

        List<PixKey> pixKeyList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PixKey pixKey = validPixKey();
            pixKeyList.add(pixKey);
        }
        validAccount.setPixKeys(pixKeyList);

        System.out.println(validCreatePixKeyDTO.getKeyType());
        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pixKeyService.createPixKey(validCreatePixKeyDTO);
        });

        assertEquals("Limit of 5 keys per account for Individuals exceeded", exception.getMessage());
    }

    @Test
    void testCreatePixKey_LegalPersonLimitExceeded() {
        CreatePixKeyDTO validCreatePixKeyDTO = validCreatePixKeyDTO();
        Account validLegalAccount = validLegalAccount();

        List<PixKey> pixKeyList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PixKey pixKey = new PixKey();
            pixKeyList.add(pixKey);
        }
        validLegalAccount.setPixKeys(pixKeyList);

        when(accountRepository.findByAgencyNumberAndAccountNumber(validCreatePixKeyDTO.getAgencyNumber(), validCreatePixKeyDTO.getAccountNumber())).thenReturn(validLegalAccount);
        when(pixKeyRepository.existsByKeyValueAndActive(validCreatePixKeyDTO.getKeyValue(), true)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pixKeyService.createPixKey(validCreatePixKeyDTO);
        });

        assertEquals("Limit of 20 keys per account for Legal Entities exceeded", exception.getMessage());
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