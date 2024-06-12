package com.cadastro.pix.service;

import com.cadastro.pix.domain.*;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Account createAccount(@Valid ReqObj reqObj) {
        User user = userRepository.findByIdentificacao(reqObj.getIdentificacao());
        if (user == null) {
            throw new EntityNotFoundException("Nao existe um user com essa identificaçao");
        }
        Account account = reqObj.toAccount();

        validateCreateAccount(account);

        account.setUser(user);
        account.setAtiva(true);

        return accountRepository.save(account);
    }

    @Transactional
    public Account updateAccount(String id, Account account) {
        UUID uuid = UUID.fromString(id);

        Account existingAccount = accountRepository.findById(uuid);
        if (existingAccount == null) {
            throw new EntityNotFoundException("Conta nao encontrada");
        }

        if (!existingAccount.isAtiva()) {
            throw new IllegalArgumentException("Conta inativa");
        }

        validateUpdatedFields(account);

        existingAccount.setTipoConta(account.getTipoConta());
        existingAccount.setNumeroAgencia(account.getNumeroAgencia());
        existingAccount.setNumeroConta(account.getNumeroConta());

        return accountRepository.save(existingAccount);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    private void validateCreateAccount(Account account) {
        validateExistAccount(account);
        validateTipoConta(account.getTipoConta());
        validateNumeroAgencia(account.getNumeroAgencia());
        validateNumeroConta(account.getNumeroConta());

    }

    private void validateUpdatedFields(Account account) {
        validateTipoConta(account.getTipoConta());
        validateNumeroAgencia(account.getNumeroAgencia());
        validateNumeroConta(account.getNumeroConta());
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
        Account existAccount = accountRepository.findByNumeroAgenciaAndNumeroConta(
                account.getNumeroAgencia(), account.getNumeroConta()
        );

        if (existAccount != null) {
            if(existAccount.isAtiva()) throw new IllegalArgumentException("Ja existe um usuario com esta identificaçao");

            throw new IllegalArgumentException("Ja existe um usuario inativo com esta identificaçao");
        }
    }
}