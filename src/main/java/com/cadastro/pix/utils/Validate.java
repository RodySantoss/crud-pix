package com.cadastro.pix.utils;

import com.cadastro.pix.controller.UserController;
import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.PixKeyRepository;
import com.cadastro.pix.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Validate {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PixKeyRepository pixKeyRepository;

    @Autowired
    private AccountRepository accountRepository;

    //USER
    public void validateCreateUser(User user) {
        logger.info("Validating user creation: {}", user);

        validateExistUser(user);
        validateUser(user);
    }

    public void validateUpdateUser(User user, String existingUserType) {
        logger.info("Validating updated fields for user: {}", user);

        validateUserType(user.getPersonType(), existingUserType);
        validateUser(user);
    }

    private void validateUser(User user) {
        validateUserName(user.getUserName());
        validateUserLastName(user.getUserLastName());
        validatePhone(user.getPhone());
        validateEmail(user.getEmail());
        validateIdentification(user);
    }

    private void validateExistUser(User user) {
        logger.info("Checking if user exists: {}", user);

        User existUser = userRepository.findByIdentification(user.getIdentification());
        if (existUser != null) {
            if (existUser.isActive()) {
                logger.error("User with this identification already exists and is active: {}", user.getIdentification());
                throw new IllegalArgumentException("User with this identification already exists and is active");
            }

            logger.error("User with this identification already exists but is inactive: {}", user.getIdentification());
            throw new IllegalArgumentException("User with this identification already exists but is inactive");
        }
    }

    private void validateUserName(String nome) {
        logger.info("Validating user name: {}", nome);

        if (nome == null || nome.isEmpty() || nome.length() > 30) {
            logger.error("Invalid user name: {}", nome);
            throw new IllegalArgumentException("Invalid user name");
        }
    }

    private void validateUserLastName(String sobrenome) {
        logger.info("Validating user last name: {}", sobrenome);

        if (sobrenome!= null && sobrenome.length() > 45) {
            logger.error("Invalid user last name: {}", sobrenome);
            throw new IllegalArgumentException("Invalid user last name");
        }
    }

    private void validateUserType(String existingUserType, String newUserType) {
        logger.info("Validating user type. Existing: {}, New: {}", existingUserType, newUserType);

        if (!existingUserType.equals(newUserType)) {
            logger.error("Attempt to change user type from {} to {}", existingUserType, newUserType);
            throw new IllegalArgumentException("It is not possible to change the person type");
        }
    }

    private void validateIdentification(User user) {
        logger.info("Validating user identification: {}", user.getIdentification());

        if (user.isIndividualPerson()) {
            validateCPF(user.getIdentification());
        } else if (user.isLegalPerson()) {
            validateCNPJ(user.getIdentification());
        } else {
            logger.error("Invalid person type for user: {}", user.getPersonType());
            throw new IllegalArgumentException("Invalid person type");
        }
    }

    //ACCOUNT

    public void validateCreateAccount(Account account) {
        // Lógica de validação
        logger.info("Validating account creation fields for account: {}", account);
        validateExistAccount(account);
        validateAccount(account);
        logger.info("Account creation fields validated successfully");
    }

    public void validateUpdateAccount(Account account) {
        logger.info("Validating account update fields for account: {}", account);
        validateAccount(account);
        logger.info("Account update fields validated successfully");
    }

    private void validateAccount(Account account) {
        validateAccountType(account.getAccountType());
        validateAgencyNumber(account.getAgencyNumber());
        validateAccountNumber(account.getAccountNumber());
    }

    private void validateAccountType(String tipoConta) {
        if (!tipoConta.equalsIgnoreCase("corrente") && !tipoConta.equalsIgnoreCase("poupança")) {
            logger.error("Invalid account type: {}", tipoConta);
            throw new IllegalArgumentException("Invalid account type");
        }
    }

    private void validateAgencyNumber(Integer numeroAgencia) {
        if (numeroAgencia == null || numeroAgencia.toString().length() > 4) {
            logger.error("Invalid agency number: {}", numeroAgencia);
            throw new IllegalArgumentException("Invalid agency number");
        }
    }

    private void validateAccountNumber(Integer numeroConta) {
        if (numeroConta == null || numeroConta.toString().length() > 8) {
            logger.error("Invalid account number: {}", numeroConta);
            throw new IllegalArgumentException("Invalid account number");
        }
    }

    private void validateExistAccount(Account account) {
        logger.info("Checking if account already exists: {}", account);
        Account existAccount = accountRepository.findByAgencyNumberAndAccountNumber(
                account.getAgencyNumber(), account.getAccountNumber()
        );

        if (existAccount != null) {
            if(existAccount.isActive()) {
                logger.error("Active account already exists with account number {} at agency {}", account.getAccountNumber(), account.getAgencyNumber());
                throw new IllegalArgumentException("There is already an account with that account number at this agency");
            }

            logger.error("Inactive account already exists with account number {} at agency {}", account.getAccountNumber(), account.getAgencyNumber());
            throw new IllegalArgumentException("There is already an inactive account with that account number at this agency");
        }
        logger.info("No existing account found with account number {} at agency {}", account.getAccountNumber(), account.getAgencyNumber());
    }

    //PIXKEY
    public void validateCreatePixKey(PixKey pixKey, List<PixKey> pixKeyList, Account account, User user) {
        logger.info("Validating PixKey creation for key: {}", pixKey);
        String keyValue = pixKey.getKeyValue();
        validateExistPixKey(keyValue);
        if (user.isIndividualPerson() && pixKeyList.size() >= 5) {
            logger.error("Limit of 5 keys per account for Individuals exceeded");
            throw new IllegalArgumentException("Limit of 5 keys per account for Individuals exceeded");
        } else if (pixKeyList.size() >= 20) {
            logger.error("Limit of 20 keys per account for Legal Entities exceeded");
            throw new IllegalArgumentException("Limit of 20 keys per account for Legal Entities exceeded");
        }

        switch (pixKey.getKeyType().toLowerCase()) {
            case "celular":
                validatePhone(keyValue);
                break;
            case "email":
                validateEmail(keyValue);
                break;
            case "cpf":
                validateCPFKey(user, keyValue, pixKeyList, account);
                break;
            case "cnpj":
                validateCNPJKey(user, keyValue, pixKeyList, account);
                break;
            case "aleatorio":
                validateRandomKey(keyValue);
                break;
            default:
                logger.error("Invalid key type: {}", pixKey.getKeyType());
                throw new IllegalArgumentException("Invalid key type");
        }
    }

    private void validateExistPixKey(String keyValue) {
        logger.info("Checking if pix key exists: {}", keyValue);

        if (pixKeyRepository.existsByKeyValueAndActive(keyValue, true)) {
            logger.error("PixKey value already registered: {}", keyValue);
            throw new IllegalArgumentException("Pix key value already registered");
        }
    }

    private void validateCPFKey(User user, String keyValue, List<PixKey> pixKeyList, Account account) {
        logger.info("Validating CPF key: {}", keyValue);

        if (user.isLegalPerson()) {
            logger.error("Legal entities cannot register a CPF key");
            throw new IllegalArgumentException("Legal entities cannot register a CPF key");
        }

        if (!account.getUser().getIdentification().equals(keyValue)) {
            logger.error("The CPF key must be the same as the account's CPF: {}", keyValue);
            throw new IllegalArgumentException("The CPF key must be the same as the account's CPF");
        }

        for (PixKey pixKey : pixKeyList) {
            if (pixKey.getKeyType().equalsIgnoreCase("cpf")) {
                logger.error("CPF key already registered for this account");
                throw new IllegalArgumentException("CPF key already registered for this account");
            }
        }

        validateCPF(keyValue);
    }

    private void validateCNPJKey(User user, String keyValue, List<PixKey> pixKeyList, Account account) {
        logger.info("Validating CNPJ key: {}", keyValue);

        if (user.isIndividualPerson()) {
            logger.error("Individuals cannot register a CNPJ key");
            throw new IllegalArgumentException("Individuals cannot register a CNPJ key");
        }

        if (!account.getUser().getIdentification().equals(keyValue)) {
            logger.error("The CNPJ key must be the same as the account's CNPJ: {}", keyValue);
            throw new IllegalArgumentException("The CNPJ key must be the same as the account's CNPJ");
        }

        for (PixKey pixKey : pixKeyList) {
            if (pixKey.getKeyType().equalsIgnoreCase("cnpj")) {
                logger.error("CNPJ key already registered for this account");
                throw new IllegalArgumentException("CNPJ key already registered for this account");
            }
        }

        validateCNPJ(keyValue);
    }


    //GENERAL

    private void validatePhone(String keyValue) {
        logger.info("Validating phone number: {}", keyValue);

        if (!keyValue.matches("^\\+\\d{1,2}\\d{1,3}\\d{9}$")) {
            logger.error("Invalid phone format: {}", keyValue);
            throw new IllegalArgumentException("Invalid phone format");
        }
    }

    private void validateEmail(String keyValue) {
        logger.info("Validating email address: {}", keyValue);

        // has to be at least in format string@string.com
        final String emailRegexPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        Pattern pattern = Pattern.compile(emailRegexPattern);
        Matcher matcher = pattern.matcher(keyValue);

        if (!matcher.matches() || keyValue.length() > 77) {
            logger.error("Invalid email format: {}", keyValue);
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validateCPF(String keyValue) {
        logger.info("Validating CPF: {}", keyValue);

        if (!isNumeric(keyValue)) {
            logger.error("CPF must only contain numbers: {}", keyValue);
            throw new IllegalArgumentException("The CPF must only contain numbers");
        }
        if (keyValue.length() != 11 || keyValue.matches("(\\d)\\1{10}")) {
            logger.error("Invalid CPF: {}", keyValue);
            throw new IllegalArgumentException("Invalid CPF");
        }
        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            digits[i] = keyValue.charAt(i) - '0';
        }
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += digits[i] * (10 - i);
        }
        int remainder = sum % 11;
        int digit1 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[9] != digit1) {
            logger.error("Invalid CPF: {}", keyValue);
            throw new IllegalArgumentException("Invalid CPF");
        }
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        remainder = sum % 11;
        int digit2 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[10] != digit2) {
            logger.error("Invalid CPF: {}", keyValue);
            throw new IllegalArgumentException("Invalid CPF");
        }
    }

    private void validateCNPJ(String keyValue) {
        logger.info("Validating CNPJ: {}", keyValue);

        if (!isNumeric(keyValue)) {
            logger.error("CNPJ must only contain numbers: {}", keyValue);
            throw new IllegalArgumentException("The CNPJ must only contain numbers");
        }
        if (keyValue.length() != 14 || keyValue.matches("(\\d)\\1{13}")) {
            logger.error("Invalid CNPJ: {}", keyValue);
            throw new IllegalArgumentException("Invalid CNPJ");
        }
        int[] digits = new int[14];
        for (int i = 0; i < 14; i++) {
            digits[i] = keyValue.charAt(i) - '0';
        }
        int sum = 0;
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 12; i++) {
            sum += digits[i] * weights1[i];
        }
        int remainder = sum % 11;
        int digit1 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[12] != digit1) {
            logger.error("Invalid CNPJ: {}", keyValue);
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
            logger.error("Invalid CNPJ: {}", keyValue);
            throw new IllegalArgumentException("Invalid CNPJ");
        }
    }

    private void validateRandomKey(String keyValue) {
        logger.info("Validating random key: {}", keyValue);
        if (!keyValue.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[4][0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")) {
            logger.error("Invalid random key: {}", keyValue);
            throw new IllegalArgumentException("Invalid random key");
        }
    }

    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            logger.error("Error parsing string to number: {}", str, e);
            return false;
        }
    }
}
