package com.cadastro.pix.controller;

import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.pixKey.CreatePixKeyDTO;
import com.cadastro.pix.dto.pixKey.PixKeyDTO;
import com.cadastro.pix.dto.pixKey.PixKeyListWithAccountAndUserDTO;
import com.cadastro.pix.dto.pixKey.PixKeyWithAccountDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.service.PixKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PixKeyControllerTest {

    @Mock
    private PixKeyService pixKeyService;

    @InjectMocks
    private PixKeyController pixKeyController;

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
        CreatePixKeyDTO newPixKey = new CreatePixKeyDTO();
        newPixKey.setKeyType("CPF");
        newPixKey.setKeyValue("12345678900");
        newPixKey.setAgencyNumber(1234);
        newPixKey.setAccountNumber(12345678);
        return newPixKey;
    }

    private PixKeyWithAccountDTO validPixKeyWithAccountDTO() {
        return new PixKeyWithAccountDTO(validPixKey());
    }

    @Test
    void testCreatePixKey_Success() {
        CreatePixKeyDTO createPixKeyDTO = validCreatePixKeyDTO();
        PixKey pixKey = validPixKey();
        PixKeyDTO pixKeyDTO = new PixKeyDTO(pixKey);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, pixKeyDTO);

        when(pixKeyService.createPixKey(any(CreatePixKeyDTO.class))).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = pixKeyController.createPixKey(createPixKeyDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(pixKeyDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testCreatePixKey_EntityNotFound() {
        when(pixKeyService.createPixKey(any(CreatePixKeyDTO.class)))
                .thenThrow(new EntityNotFoundException("There is no such account with this agency number and account"));

        ResponseEntity<RespDTO> response = pixKeyController.createPixKey(validCreatePixKeyDTO());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(
                "There is no such account with this agency number and account",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testCreatePixKey_IllegalArgumentException() {
        CreatePixKeyDTO pixKeyDTO = validCreatePixKeyDTO();

        when(pixKeyService.createPixKey(any(CreatePixKeyDTO.class)))
                .thenThrow(new IllegalArgumentException("Pix key value already registered"));

        ResponseEntity<RespDTO> response = pixKeyController.createPixKey(pixKeyDTO);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(
                "Pix key value already registered",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testFindAllPixKeys_Success() {
        List<PixKey> pixKeys = Arrays.asList(validPixKey(), validPixKey());
        PixKeyListWithAccountAndUserDTO pixKeysDTO = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, pixKeysDTO);

        when(pixKeyService.findAll()).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = pixKeyController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(pixKeysDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testFindAllPixKeys_EntityNotFound() {
        when(pixKeyService.findAll()).thenThrow(new EntityNotFoundException("No Pix keys found"));

        ResponseEntity<RespDTO> response = pixKeyController.findAll();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No Pix keys found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testFindPixKeyById_Success() {
        UUID id = UUID.randomUUID();
        PixKey pixKey = validPixKey();
        PixKeyWithAccountDTO pixKeyDTO = new PixKeyWithAccountDTO(pixKey);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, pixKeyDTO);

        when(pixKeyService.findById(any(UUID.class))).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = pixKeyController.findById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(pixKeyDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testFindPixKeyById_EntityNotFound() {
        UUID id = UUID.randomUUID();
        when(pixKeyService.findById(id)).thenThrow(new EntityNotFoundException("Pix key not found"));

        ResponseEntity<RespDTO> response = pixKeyController.findById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Pix key not found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testFindPixKeysByType_Success() {
        String keyType = "CPF";
        List<PixKey> pixKeys = Arrays.asList(validPixKey(), validPixKey());
        PixKeyListWithAccountAndUserDTO pixKeysDTO = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, pixKeysDTO);

        when(pixKeyService.findByType(keyType)).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = pixKeyController.findByType(keyType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(pixKeysDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testFindPixKeysByType_EntityNotFound() {
        String keyType = "CPF";
        when(pixKeyService.findByType(keyType))
                .thenThrow(new EntityNotFoundException("No pix keys found for the specified type"));

        ResponseEntity<RespDTO> response = pixKeyController.findByType(keyType);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(
                "No pix keys found for the specified type",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testFindPixKeysByAgencyAndAccount_Success() {
        Integer agency = 1234;
        Integer account = 12345678;
        List<PixKey> pixKeys = Arrays.asList(validPixKey(), validPixKey());
        PixKeyListWithAccountAndUserDTO pixKeysDTO = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, pixKeysDTO);

        when(pixKeyService.findByAgencyAndAccount(any(Integer.class), any(Integer.class))).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = pixKeyController.findByAgencyAndAccount(agency, account);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(pixKeysDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testFindPixKeysByAgencyAndAccount_EntityNotFound() {
        Integer agency = 1234;
        Integer account = 12345678;

        when(pixKeyService.findByAgencyAndAccount(any(Integer.class), any(Integer.class)))
                .thenThrow(new EntityNotFoundException("There is no such account with this agency number and account"));

        ResponseEntity<RespDTO> response = pixKeyController.findByAgencyAndAccount(agency, account);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(
                "There is no such account with this agency number and account",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testFindPixKeysByUserName_Success() {
        String userName = "Joao";
        List<PixKey> pixKeys = Arrays.asList(validPixKey(), validPixKey());
        PixKeyListWithAccountAndUserDTO pixKeysDTO = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, pixKeysDTO);

        when(pixKeyService.findByUserName(any(String.class))).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = pixKeyController.findByUserName(userName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(pixKeysDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testFindPixKeysByUserName_EntityNotFound() {
        String userName = "Joao";

        when(pixKeyService.findByUserName(any(String.class)))
                .thenThrow(new EntityNotFoundException("There is no user with that name"));

        ResponseEntity<RespDTO> response = pixKeyController.findByUserName(userName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(
                "There is no user with that name",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testFindPixKeysByCreatedAt_Success() {
        LocalDate createdAt = LocalDate.now();
        List<PixKey> pixKeys = Arrays.asList(validPixKey(), validPixKey());
        PixKeyListWithAccountAndUserDTO pixKeysDTO = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, pixKeysDTO);

        when(pixKeyService.findByCreatedAt(any(LocalDate.class))).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = pixKeyController.findByCreatedAt(createdAt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(pixKeysDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testFindPixKeysByCreatedAt_EntityNotFound() {
        LocalDate createdAt = LocalDate.now();

        when(pixKeyService.findByCreatedAt(any(LocalDate.class)))
                .thenThrow(new EntityNotFoundException("No Pix keys found on that date"));

        ResponseEntity<RespDTO> response = pixKeyController.findByCreatedAt(createdAt);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(
                "No Pix keys found on that date",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testFindPixKeysByInactivatedAt_Success() {
        LocalDate inactivatedAt = LocalDate.now();
        List<PixKey> pixKeys = Arrays.asList(validPixKey(), validPixKey());
        PixKeyListWithAccountAndUserDTO pixKeysDTO = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, pixKeysDTO);

        when(pixKeyService.findByInactivatedAt(any(LocalDate.class))).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = pixKeyController.findByInactivatedAt(inactivatedAt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(pixKeysDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testFindPixKeysByInactivatedAt_EntityNotFound() {
        LocalDate inactivatedAt = LocalDate.now();

        when(pixKeyService.findByInactivatedAt(any(LocalDate.class)))
                .thenThrow(new EntityNotFoundException("There is no pix key inactivated on this date"));

        ResponseEntity<RespDTO> response = pixKeyController.findByInactivatedAt(inactivatedAt);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(
                "There is no pix key inactivated on this date",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testDeletePixKey_Success() {
        UUID id = UUID.randomUUID();
        PixKey pixKey = validPixKey();
        PixKeyDTO pixKeyDTO = new PixKeyDTO(pixKey);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, pixKeyDTO);

        when(pixKeyService.deletePixKey(id)).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = pixKeyController.deletePixKey(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(pixKeyDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testDeletePixKey_EntityNotFound() {
        UUID id = UUID.randomUUID();
        when(pixKeyService.deletePixKey(id)).thenThrow(new EntityNotFoundException("Pix key not found"));

        ResponseEntity<RespDTO> response = pixKeyController.deletePixKey(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Pix key not found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testDeletePixKey_IllegalArgumentException() {
        UUID id = UUID.randomUUID();
        when(pixKeyService.deletePixKey(id)).thenThrow(new IllegalArgumentException("Pix key is already inactive"));

        ResponseEntity<RespDTO> response = pixKeyController.deletePixKey(id);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Pix key is already inactive", Objects.requireNonNull(response.getBody()).getMessage());
    }
}
