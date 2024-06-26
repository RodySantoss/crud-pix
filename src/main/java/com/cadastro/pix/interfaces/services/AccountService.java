package com.cadastro.pix.interfaces.services;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.account.CreateAccountDTO;
import com.cadastro.pix.dto.account.SimpleAccountListWithUserDTO;
import com.cadastro.pix.dto.account.SimpleAccountWithUserDTO;
import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AccountService {
    public RespDTO createAccount(CreateAccountDTO newAccountDTO);

    public RespDTO findAllAccounts();

    public RespDTO findAccountById(UUID id);

    public RespDTO updateAccount(UUID id, Account account);

    public RespDTO deleteAccount(UUID id);
}
