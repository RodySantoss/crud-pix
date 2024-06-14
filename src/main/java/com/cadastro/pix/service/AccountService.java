package com.cadastro.pix.service;

import com.cadastro.pix.domain.*;
import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.account.dto.SimpleAccountListWithUserDTO;
import com.cadastro.pix.domain.account.dto.SimpleAccountWithUserDTO;
import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.domain.pixKey.dto.PixKeyDTO;
import com.cadastro.pix.domain.pixKey.dto.PixKeyListWithAccountAndUserDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.domain.user.dto.UserDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public RespDTO createAccount(@Valid ReqObj reqObj) {
        User user = userRepository.findByIdentification(reqObj.getIdentification());
        if (user == null) {
            throw new EntityNotFoundException("Nao existe um user com essa identificaçao");
        }
        Account account = reqObj.toAccount();

        validateCreateAccount(account);

        account.setUser(user);
        account.setActive(true);

        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(accountRepository.save(account).getId());
        return new RespDTO(HttpStatus.OK, accountDTO);
    }

    @Transactional
    public RespDTO updateAccount(String id, Account account) {
        UUID uuid = UUID.fromString(id);

        Account existingAccount = accountRepository.findById(uuid);
        if (existingAccount == null) {
            throw new EntityNotFoundException("Conta nao encontrada");
        }

        if (!existingAccount.isActive()) {
            throw new IllegalArgumentException("Conta inativa");
        }

        validateUpdatedFields(account);

        existingAccount.setAccountType(account.getAccountType());
        existingAccount.setAgencyNumber(account.getAgencyNumber());
        existingAccount.setAccountNumber(account.getAccountNumber());


        Account updatedAccount = accountRepository.save(existingAccount);
        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(updatedAccount);
        return new RespDTO(HttpStatus.OK, accountDTO);
    }

    @Transactional
    public RespDTO deleteAccount(UUID id) {
        Account existingAccount = accountRepository.findById(id);
        if (existingAccount == null) {
            throw new EntityNotFoundException("Conta nao encontrada");
        }

        if (!existingAccount.isActive()) {
            throw new IllegalArgumentException("Conta ja esta inativa");
        }

        // Inativa a chave Pix e registra a data e hora da inativação
        existingAccount.setActive(false);
        existingAccount.setInactivatedAt(LocalDateTime.now());

        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(accountRepository.save(existingAccount));
        return new RespDTO(HttpStatus.OK, accountDTO);
    }

    public RespDTO findAllAccounts() {
        List<Account> accounts = accountRepository.findAll();

        System.out.println(accounts.size());

        SimpleAccountListWithUserDTO accountListDTO = SimpleAccountListWithUserDTO.fromAccounts(accounts);
        return new RespDTO(HttpStatus.OK, accountListDTO);
    }

    public RespDTO findAccountById(UUID id) {
        Account existingAccount = accountRepository.findById(id);
        if (existingAccount == null) {
            throw new EntityNotFoundException("Conta nao encontrada");
        }

        SimpleAccountWithUserDTO accountDTO = new SimpleAccountWithUserDTO(accountRepository.save(existingAccount));
        return new RespDTO(HttpStatus.OK, accountDTO);
    }

    private void validateCreateAccount(Account account) {
        validateExistAccount(account);
        validateTipoConta(account.getAccountType());
        validateNumeroAgencia(account.getAgencyNumber());
        validateNumeroConta(account.getAccountNumber());

    }

    private void validateUpdatedFields(Account account) {
        validateTipoConta(account.getAccountType());
        validateNumeroAgencia(account.getAgencyNumber());
        validateNumeroConta(account.getAccountNumber());
    }

    private void validateTipoConta(String tipoConta) {
        if (!tipoConta.equalsIgnoreCase("corrente") && !tipoConta.equalsIgnoreCase("poupança")) {
            throw new IllegalArgumentException("Tipo de conta inválido");
        }
    }

    private void validateNumeroAgencia(Integer numeroAgencia) {
        if (numeroAgencia == null || numeroAgencia.toString().length() > 4) {
            throw new IllegalArgumentException("Número da agência inválido");
        }
    }

    private void validateNumeroConta(Integer numeroConta) {
        if (numeroConta == null || numeroConta.toString().length() > 8) {
            throw new IllegalArgumentException("Número da conta inválido");
        }
    }


    private void validateExistAccount(Account account) {
        Account existAccount = accountRepository.findByAgencyNumberAndAccountNumber(
                account.getAgencyNumber(), account.getAccountNumber()
        );

        if (existAccount != null) {
            if(existAccount.isActive()) throw new IllegalArgumentException("Ja existe uma conta com esse numero de conta nessa agencia");

            throw new IllegalArgumentException("Ja existe uma conta inativa com esse numero de conta nessa agencia");
        }
    }
}