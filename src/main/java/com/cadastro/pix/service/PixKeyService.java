package com.cadastro.pix.service;

import com.cadastro.pix.domain.*;
import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.dto.pixKey.CreatePixKeyDTO;
import com.cadastro.pix.dto.pixKey.PixKeyDTO;
import com.cadastro.pix.dto.pixKey.PixKeyListWithAccountAndUserDTO;
import com.cadastro.pix.dto.pixKey.PixKeyWithAccountDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.PixKeyRepository;
import com.cadastro.pix.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PixKeyService {

    private static final Logger log = LoggerFactory.getLogger(PixKeyService.class);

    @Autowired
    private PixKeyRepository pixKeyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public RespDTO createPixKey(@Valid CreatePixKeyDTO createPixKeyDTO) {
        log.info("Starting PixKey creation process for request: {}", createPixKeyDTO);
        Account account = accountRepository.findByAgencyNumberAndAccountNumber(createPixKeyDTO.getAgencyNumber(), createPixKeyDTO.getAccountNumber());
        if (account == null) {
            log.error("Account not found with agency number {} and account number {}", createPixKeyDTO.getAgencyNumber(), createPixKeyDTO.getAccountNumber());
            throw new EntityNotFoundException("There is no such account with this agency number and account");
        }
        User user = account.getUser();
        PixKey pixKey = new PixKey(createPixKeyDTO);

        List<PixKey> pixKeyList = account.getPixKeys();
        int pixKeyListSize = pixKeyList.size();

        validateCreatePixKey(pixKey, pixKeyList, account, user);

        if (user.isIndividualPerson() && pixKeyListSize >= 5) {
            log.error("Limit of 5 keys per account for Individuals exceeded");
            throw new IllegalArgumentException("Limit of 5 keys per account for Individuals exceeded");
        } else if (user.isLegalPerson() && pixKeyListSize >= 20) {
            log.error("Limit of 20 keys per account for Legal Entities exceeded");
            throw new IllegalArgumentException("Limit of 20 keys per account for Legal Entities exceeded");
        }

        pixKey.setActive(true); // Por padrão, nova chave PIX é ativa
        pixKey.setAccount(account);

        PixKeyDTO pixKeyDTO = new PixKeyDTO(pixKeyRepository.save(pixKey).getId());
        log.info("PixKey created successfully: {}", pixKeyDTO);
        return new RespDTO(HttpStatus.OK, pixKeyDTO);
    }

    @Transactional
    public RespDTO findAll() {
        log.info("Finding all PixKeys");
        List<PixKey> pixKeys = pixKeyRepository.findAll();
        if (pixKeys.isEmpty()) {
            log.error("No PixKeys found");
            throw new EntityNotFoundException("No Pix keys found");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        log.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    @Transactional
    public RespDTO findById(UUID id) {
        log.info("Finding PixKey by id: {}", id);
        PixKey pixKey = pixKeyRepository.findById(id).orElse(null);
        if (pixKey == null) {
            log.error("PixKey not found with id: {}", id);
            throw new EntityNotFoundException("Pix key not found");
        }

        PixKeyWithAccountDTO pixKeyDTO = new PixKeyWithAccountDTO(pixKey);
        log.info("PixKey found: {}", pixKeyDTO);
        return new RespDTO(HttpStatus.OK, pixKeyDTO);
    }

    @Transactional
    public RespDTO findByType(String keyType) {
        log.info("Finding PixKeys by type: {}", keyType);
        List<PixKey> pixKeys = pixKeyRepository.findByKeyType(keyType);
        if (pixKeys.isEmpty()) {
            log.error("No PixKeys found for type: {}", keyType);
            throw new EntityNotFoundException("No pix keys found for the specified type");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        log.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    @Transactional
    public RespDTO findByAgencyAndAccount(int agencyNumber, int accountNumber) {
        log.info("Finding PixKeys by agency number: {} and account number: {}", agencyNumber, accountNumber);
        Account account = accountRepository.findByAgencyNumberAndAccountNumber(agencyNumber, accountNumber);
        if (account == null) {
            log.error("Account not found with agency number: {} and account number: {}", agencyNumber, accountNumber);
            throw new EntityNotFoundException("There is no such account with this agency number and account");
        }

        List<PixKey> pixKeys = account.getPixKeys();
        if (pixKeys.isEmpty()) {
            log.error("No PixKeys found for account with agency number: {} and account number: {}", agencyNumber, accountNumber);
            throw new EntityNotFoundException("No pix keys found for the specified type");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys, account);
        log.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    public RespDTO findByUserName(String userName) {
        log.info("Finding PixKeys by user name: {}", userName);
        List<PixKey> pixKeys = pixKeyRepository.findByUserName(userName);
        if (pixKeys.isEmpty()) {
            log.error("No PixKeys found for user name: {}", userName);
            throw new EntityNotFoundException("There is no user with that name");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        log.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    public RespDTO findByCreatedAt(LocalDate date) {
        log.info("Finding PixKeys by creation date: {}", date);
        if (date == null) {
            log.error("Creation date must be provided for consultation");
            return new RespDTO(HttpStatus.BAD_REQUEST, "The inclusion date must be provided for consultation");
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PixKey> pixKeys = pixKeyRepository.findByCreatedAtBetween(startOfDay, endOfDay);
        if (pixKeys.isEmpty()) {
            log.error("No PixKeys found for creation date: {}", date);
            throw new EntityNotFoundException("No Pix keys found on that date");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        log.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    public RespDTO findByInactivatedAt(LocalDate date) {
        log.info("Finding PixKeys by inactivation date: {}", date);
        if (date == null) {
            log.error("Inactivation date must be provided for consultation");
            return new RespDTO(HttpStatus.BAD_REQUEST, "The inactivation date must be provided for consultation");
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PixKey> pixKeys = pixKeyRepository.findByInactivatedAtBetween(startOfDay, endOfDay);
        if (pixKeys.isEmpty()) {
            log.error("No PixKeys found for inactivation date: {}", date);
            throw new EntityNotFoundException("There is no pix key inactivated on this date");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        log.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    @Transactional
    public RespDTO deletePixKey(UUID id) {
        log.info("Starting PixKey deletion process for id: {}", id);
        PixKey existingPixKey = pixKeyRepository.findById(id).orElse(null);
        if (existingPixKey == null) {
            log.error("PixKey not found with id: {}", id);
            throw new EntityNotFoundException("Pix key not found");
        }

        if (!existingPixKey.isActive()) {
            log.error("Attempt to delete an already inactive PixKey with id: {}", id);
            throw new IllegalArgumentException("Pix key is already inactive");
        }

        existingPixKey.setActive(false);
        existingPixKey.setInactivatedAt(LocalDateTime.now());

        pixKeyRepository.save(existingPixKey);
        PixKeyDTO pixKeyDTO = new PixKeyDTO(existingPixKey);
        log.info("PixKey deleted successfully: {}", pixKeyDTO);
        return new RespDTO(HttpStatus.OK, pixKeyDTO);
    }

    private void validateCreatePixKey(PixKey pixKey, List<PixKey> pixKeyList, Account account, User user) {
        log.info("Validating PixKey creation for key: {}", pixKey);
        String keyValue = pixKey.getKeyValue();
        if (pixKeyRepository.existsByKeyValueAndActive(keyValue, true)) {
            log.error("PixKey value already registered: {}", keyValue);
            throw new IllegalArgumentException("Pix key value already registered");
        }

        switch (pixKey.getKeyType().toLowerCase()) {
            case "celular":
                validateCelular(keyValue);
                break;
            case "email":
                validateEmail(keyValue);
                break;
            case "cpf":
                if (user.isLegalPerson()) {
                    log.error("Legal entities cannot register a CPF key");
                    throw new IllegalArgumentException("Legal entities cannot register a CPF key");
                }
                validateCPF(keyValue, pixKeyList, account);
                break;
            case "cnpj":
                if (user.isIndividualPerson()) {
                    log.error("Individuals cannot register a CNPJ key");
                    throw new IllegalArgumentException("Individuals cannot register a CNPJ key");
                }
                validateCNPJ(keyValue, pixKeyList, account);
                break;
            case "aleatorio":
                validateRandomKey(keyValue);
                break;
            default:
                log.error("Invalid key type: {}", pixKey.getKeyType());
                throw new IllegalArgumentException("Invalid key type");
        }
    }

    private void validateCelular(String valorChave) {
        log.info("Validating cellular key: {}", valorChave);
        if (!valorChave.matches("^\\+\\d{1,2}\\d{1,3}\\d{9}$")) {
            log.error("Invalid phone format: {}", valorChave);
            throw new IllegalArgumentException("Invalid phone format");
        }
    }

    private void validateEmail(String valorChave) {
        log.info("Validating email key: {}", valorChave);

        // has to be at least in format string@string.com
        final String emailRegexPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        Pattern pattern = Pattern.compile(emailRegexPattern);
        Matcher matcher = pattern.matcher(valorChave);

        if (!matcher.matches() || valorChave.length() > 77) {
            log.error("Invalid email format: {}", valorChave);
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validateCPF(String valorChave, List<PixKey> pixKeyList, Account account) {
        log.info("Validating CPF key: {}", valorChave);
        if (!account.getUser().getIdentification().equals(valorChave)) {
            log.error("The CPF key must be the same as the account's CPF: {}", valorChave);
            throw new IllegalArgumentException("The CPF key must be the same as the account's CPF");
        }

        for (PixKey pixKey : pixKeyList) {
            if (pixKey.getKeyType().equalsIgnoreCase("cpf")) {
                log.error("CPF key already registered for this account");
                throw new IllegalArgumentException("CPF key already registered for this account");
            }
        }
        if (valorChave.length() != 11 || valorChave.matches("(\\d)\\1{10}")) {
            log.error("Invalid CPF: {}", valorChave);
            throw new IllegalArgumentException("Invalid CPF");
        }
        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            digits[i] = valorChave.charAt(i) - '0';
        }
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += digits[i] * (10 - i);
        }
        int remainder = sum % 11;
        int digit1 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[9] != digit1) {
            log.error("Invalid CPF: {}", valorChave);
            throw new IllegalArgumentException("Invalid CPF");
        }
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        remainder = sum % 11;
        int digit2 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[10] != digit2) {
            log.error("Invalid CPF: {}", valorChave);
            throw new IllegalArgumentException("Invalid CPF");
        }
    }

    private void validateCNPJ(String valorChave, List<PixKey> pixKeyList, Account account) {
        log.info("Validating CNPJ key: {}", valorChave);
        if (!account.getUser().getIdentification().equals(valorChave)) {
            log.error("The CNPJ key must be the same as the account's CNPJ: {}", valorChave);
            throw new IllegalArgumentException("The CNPJ key must be the same as the account's CNPJ");
        }

        for (PixKey pixKey : pixKeyList) {
            if (pixKey.getKeyType().equalsIgnoreCase("cnpj")) {
                log.error("CNPJ key already registered for this account");
                throw new IllegalArgumentException("CNPJ key already registered for this account");
            }
        }

        valorChave = valorChave.replaceAll("[^0-9]", "");
        if (valorChave.length() != 14 || valorChave.matches("(\\d)\\1{13}")) {
            log.error("Invalid CNPJ: {}", valorChave);
            throw new IllegalArgumentException("Invalid CNPJ");
        }
        int[] digits = new int[14];
        for (int i = 0; i < 14; i++) {
            digits[i] = valorChave.charAt(i) - '0';
        }
        int sum = 0;
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 12; i++) {
            sum += digits[i] * weights1[i];
        }
        int remainder = sum % 11;
        int digit1 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[12] != digit1) {
            log.error("Invalid CNPJ: {}", valorChave);
            throw new IllegalArgumentException("Invalid CNPJ");
        }
        sum = 0;
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 13; i++) {
            sum += digits[i] * weights2[i];
        }
        remainder = sum % 11;
        int digit2 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[13] != digit2) {
            log.error("Invalid CNPJ: {}", valorChave);
            throw new IllegalArgumentException("Invalid CNPJ");
        }
    }

    private void validateRandomKey(String valorChave) {
        log.info("Validating random key: {}", valorChave);
        if (!valorChave.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[4][0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")) {
            log.error("Invalid random key: {}", valorChave);
            throw new IllegalArgumentException("Invalid random key");
        }
    }
}