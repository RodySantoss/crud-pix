package com.cadastro.pix.service;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.dto.pixKey.CreatePixKeyDTO;
import com.cadastro.pix.dto.pixKey.PixKeyDTO;
import com.cadastro.pix.dto.pixKey.PixKeyListWithAccountAndUserDTO;
import com.cadastro.pix.dto.pixKey.PixKeyWithAccountDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.PixKeyRepository;
import com.cadastro.pix.repository.UserRepository;
import com.cadastro.pix.utils.Validate;
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

    private static final Logger logger = LoggerFactory.getLogger(PixKeyService.class);

    @Autowired
    private PixKeyRepository pixKeyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validate validate;

    @Transactional
    public RespDTO createPixKey(@Valid CreatePixKeyDTO createPixKeyDTO) {
        logger.info("Starting PixKey creation process for request: {}", createPixKeyDTO);
        Account account = accountRepository.findByAgencyNumberAndAccountNumber(createPixKeyDTO.getAgencyNumber(), createPixKeyDTO.getAccountNumber());
        if (account == null) {
            logger.error("Account not found with agency number {} and account number {}", createPixKeyDTO.getAgencyNumber(), createPixKeyDTO.getAccountNumber());
            throw new EntityNotFoundException("There is no such account with this agency number and account");
        }
        User user = account.getUser();
        PixKey pixKey = new PixKey(createPixKeyDTO);

        List<PixKey> pixKeyList = account.getPixKeys();
        int pixKeyListSize = pixKeyList.size();

        validate.validateCreatePixKey(pixKey, pixKeyList, account, user);

        if (user.isIndividualPerson() && pixKeyListSize >= 5) {
            logger.error("Limit of 5 keys per account for Individuals exceeded");
            throw new IllegalArgumentException("Limit of 5 keys per account for Individuals exceeded");
        } else if (user.isLegalPerson() && pixKeyListSize >= 20) {
            logger.error("Limit of 20 keys per account for Legal Entities exceeded");
            throw new IllegalArgumentException("Limit of 20 keys per account for Legal Entities exceeded");
        }

        pixKey.setActive(true); // Por padrão, nova chave PIX é ativa
        pixKey.setAccount(account);

        PixKeyDTO pixKeyDTO = new PixKeyDTO(pixKeyRepository.save(pixKey).getId());
        logger.info("PixKey created successfully: {}", pixKeyDTO);
        return new RespDTO(HttpStatus.OK, pixKeyDTO);
    }

    @Transactional
    public RespDTO findAll() {
        logger.info("Finding all PixKeys");
        List<PixKey> pixKeys = pixKeyRepository.findAll();
        if (pixKeys.isEmpty()) {
            logger.error("No PixKeys found");
            throw new EntityNotFoundException("No Pix keys found");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        logger.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    @Transactional
    public RespDTO findById(UUID id) {
        logger.info("Finding PixKey by id: {}", id);
        PixKey pixKey = pixKeyRepository.findById(id).orElse(null);
        if (pixKey == null) {
            logger.error("PixKey not found with id: {}", id);
            throw new EntityNotFoundException("Pix key not found");
        }

        PixKeyWithAccountDTO pixKeyDTO = new PixKeyWithAccountDTO(pixKey);
        logger.info("PixKey found: {}", pixKeyDTO);
        return new RespDTO(HttpStatus.OK, pixKeyDTO);
    }

    @Transactional
    public RespDTO findByType(String keyType) {
        logger.info("Finding PixKeys by type: {}", keyType);
        List<PixKey> pixKeys = pixKeyRepository.findByKeyType(keyType);
        if (pixKeys.isEmpty()) {
            logger.error("No PixKeys found for type: {}", keyType);
            throw new EntityNotFoundException("No pix keys found for the specified type");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        logger.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    @Transactional
    public RespDTO findByAgencyAndAccount(int agencyNumber, int accountNumber) {
        logger.info("Finding PixKeys by agency number: {} and account number: {}", agencyNumber, accountNumber);
        Account account = accountRepository.findByAgencyNumberAndAccountNumber(agencyNumber, accountNumber);
        if (account == null) {
            logger.error("Account not found with agency number: {} and account number: {}", agencyNumber, accountNumber);
            throw new EntityNotFoundException("There is no such account with this agency number and account");
        }

        List<PixKey> pixKeys = account.getPixKeys();
        if (pixKeys.isEmpty()) {
            logger.error("No PixKeys found for account with agency number: {} and account number: {}", agencyNumber, accountNumber);
            throw new EntityNotFoundException("No pix keys found for the specified type");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys, account);
        logger.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    public RespDTO findByUserName(String userName) {
        logger.info("Finding PixKeys by user name: {}", userName);
        List<PixKey> pixKeys = pixKeyRepository.findByUserName(userName);
        if (pixKeys.isEmpty()) {
            logger.error("No PixKeys found for user name: {}", userName);
            throw new EntityNotFoundException("There is no user with that name");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        logger.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    public RespDTO findByCreatedAt(LocalDate date) {
        logger.info("Finding PixKeys by creation date: {}", date);
        if (date == null) {
            logger.error("Creation date must be provided for consultation");
            return new RespDTO(HttpStatus.BAD_REQUEST, "The inclusion date must be provided for consultation");
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PixKey> pixKeys = pixKeyRepository.findByCreatedAtBetween(startOfDay, endOfDay);
        if (pixKeys.isEmpty()) {
            logger.error("No PixKeys found for creation date: {}", date);
            throw new EntityNotFoundException("No Pix keys found on that date");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        logger.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    public RespDTO findByInactivatedAt(LocalDate date) {
        logger.info("Finding PixKeys by inactivation date: {}", date);
        if (date == null) {
            logger.error("Inactivation date must be provided for consultation");
            return new RespDTO(HttpStatus.BAD_REQUEST, "The inactivation date must be provided for consultation");
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PixKey> pixKeys = pixKeyRepository.findByInactivatedAtBetween(startOfDay, endOfDay);
        if (pixKeys.isEmpty()) {
            logger.error("No PixKeys found for inactivation date: {}", date);
            throw new EntityNotFoundException("There is no pix key inactivated on this date");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        logger.info("PixKeys found. Size: {}", pixKeys.size());
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    @Transactional
    public RespDTO deletePixKey(UUID id) {
        logger.info("Starting PixKey deletion process for id: {}", id);
        PixKey existingPixKey = pixKeyRepository.findById(id).orElse(null);
        if (existingPixKey == null) {
            logger.error("PixKey not found with id: {}", id);
            throw new EntityNotFoundException("Pix key not found");
        }

        if (!existingPixKey.isActive()) {
            logger.error("Attempt to delete an already inactive PixKey with id: {}", id);
            throw new IllegalArgumentException("Pix key is already inactive");
        }

        existingPixKey.setActive(false);
        existingPixKey.setInactivatedAt(LocalDateTime.now());

        pixKeyRepository.save(existingPixKey);
        PixKeyDTO pixKeyDTO = new PixKeyDTO(existingPixKey);
        logger.info("PixKey deleted successfully: {}", pixKeyDTO);
        return new RespDTO(HttpStatus.OK, pixKeyDTO);
    }
}