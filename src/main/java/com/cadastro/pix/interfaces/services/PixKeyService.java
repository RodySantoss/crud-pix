package com.cadastro.pix.interfaces.services;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.pixKey.CreatePixKeyDTO;
import com.cadastro.pix.dto.pixKey.PixKeyDTO;
import com.cadastro.pix.dto.pixKey.PixKeyListWithAccountAndUserDTO;
import com.cadastro.pix.dto.pixKey.PixKeyWithAccountDTO;
import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface PixKeyService {
    public RespDTO createPixKey(CreatePixKeyDTO createPixKeyDTO);

    public RespDTO findAllPixKeys();

    public RespDTO findPixKeyById(UUID id);

    public RespDTO findPixKeysByType(String keyType);

    public RespDTO findPixKeysByAgencyAndAccount(int agencyNumber, int accountNumber);

    public RespDTO findPixKeysByUserName(String userName);

    public RespDTO findPixKeysByCreatedAt(LocalDate date);

    public RespDTO findPixKeysByInactivatedAt(LocalDate date);

    public RespDTO deletePixKey(UUID id);
}
