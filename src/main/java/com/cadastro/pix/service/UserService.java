package com.cadastro.pix.service;

import com.cadastro.pix.controller.UserController;
import com.cadastro.pix.domain.RespDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.user.UserDTO;
import com.cadastro.pix.dto.user.UserListDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public RespDTO createUser(@Valid User user) {
        logger.info("Starting user creation process for user: {}", user);

        validateCreateUser(user);
        user.setActive(true);
        UserDTO userDTO = new UserDTO(userRepository.save(user).getId());

        logger.info("User created successfully: {}", userDTO);
        return new RespDTO(HttpStatus.OK, userDTO);
    }

    public RespDTO findAllUsers() {
        logger.info("Fetching all users");

        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            log.error("No users found");
            throw new EntityNotFoundException("No users found");
        }

        UserListDTO usersDTO = UserListDTO.fromUsers(users);

        logger.info("Found {} users", users.size());
        return new RespDTO(HttpStatus.OK, usersDTO);
    }

    public RespDTO findUserById(UUID id) {
        logger.info("Fetching user by ID: {}", id);

        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            userNotFoundForId(id);
        }

        UserDTO userDTO = new UserDTO(existingUser);
        logger.info("User found: {}", userDTO);
        return new RespDTO(HttpStatus.OK, userDTO);
    }

    @Transactional
    public RespDTO updateUser(UUID id, User user) {
        logger.info("Starting user update process for user ID: {}", id);

        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            userNotFoundForId(id);
        }

        if (!existingUser.isActive()) {
            logger.error("Attempt to update inactive user ID: {}", id);
            throw new IllegalArgumentException("User is inactive");
        }

        validateUpdatedFields(user, existingUser.getPersonType());

        existingUser.setUserName(user.getUserName());
        existingUser.setUserLastName(user.getUserLastName());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());

        User updatedUser = userRepository.save(existingUser);
        UserDTO userDTO = new UserDTO(updatedUser);

        logger.info("User updated successfully: {}", userDTO);
        return new RespDTO(HttpStatus.OK, userDTO);
    }

    @Transactional
    public RespDTO deleteUser(UUID id) {
        logger.info("Starting user deletion process for user ID: {}", id);

        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            userNotFoundForId(id);
        }

        if (!existingUser.isActive()) {
            logger.error("Attempt to delete inactive user ID: {}", id);
            throw new IllegalArgumentException("User is already inactive");
        }
        existingUser.setActive(false);
        existingUser.setInactivatedAt(LocalDateTime.now());

        userRepository.save(existingUser);

        UserDTO userDTO = new UserDTO(existingUser);

        logger.info("User deactivated successfully: {}", userDTO);
        return new RespDTO(HttpStatus.OK, userDTO);
    }

    private void validateCreateUser(User user) {
        logger.info("Validating user creation: {}", user);

        validateExistUser(user);
        validateNomeCorrentista(user.getUserName());
        validateSobrenomeCorrentista(user.getUserLastName());
        validateCelular(user.getPhone());
        validateEmail(user.getEmail());
        validateIdentificacao(user);
    }

    private void validateUpdatedFields(User user, String existingUserType) {
        logger.info("Validating updated fields for user: {}", user);

        validateUserType(user.getPersonType(), existingUserType);
        validateNomeCorrentista(user.getUserName());
        validateSobrenomeCorrentista(user.getUserLastName());
        validateCelular(user.getPhone());
        validateEmail(user.getEmail());
        validateIdentificacao(user);
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

    private void validateUserType(String existingUserType, String newUserType) {
        logger.info("Validating user type. Existing: {}, New: {}", existingUserType, newUserType);

        if (!existingUserType.equals(newUserType)) {
            logger.error("Attempt to change user type from {} to {}", existingUserType, newUserType);
            throw new IllegalArgumentException("It is not possible to change the person type");
        }
    }

    private void validateNomeCorrentista(String nome) {
        logger.info("Validating account holder name: {}", nome);

        if (nome == null || nome.isEmpty() || nome.length() > 30) {
            logger.error("Invalid account holder name: {}", nome);
            throw new IllegalArgumentException("Invalid account holder name");
        }
    }

    private void validateSobrenomeCorrentista(String sobrenome) {
        logger.info("Validating account holder last name: {}", sobrenome);

        if (sobrenome.length() > 45) {
            logger.error("Invalid account holder last name: {}", sobrenome);
            throw new IllegalArgumentException("Invalid account holder last name");
        }
    }

    private void validateCelular(String valorChave) {
        logger.info("Validating phone number: {}", valorChave);

        if (!valorChave.matches("^\\+\\d{1,2}\\d{1,3}\\d{9}$")) {
            logger.error("Invalid phone format: {}", valorChave);
            throw new IllegalArgumentException("Invalid phone format");
        }
    }

    private void validateEmail(String valorChave) {
        logger.info("Validating email address: {}", valorChave);

        // has to be at least in format string@string.com
        final String emailRegexPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        Pattern pattern = Pattern.compile(emailRegexPattern);
        Matcher matcher = pattern.matcher(valorChave);

        if (!matcher.matches() || valorChave.length() > 77) {
            logger.error("Invalid email format: {}", valorChave);
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validateIdentificacao(User user) {
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

    private void validateCPF(String valorChave) {
        logger.info("Validating CPF: {}", valorChave);

        if (!isNumeric(valorChave)) {
            logger.error("CPF must only contain numbers: {}", valorChave);
            throw new IllegalArgumentException("The CPF must only contain numbers");
        }
        if (valorChave.length() != 11 || valorChave.matches("(\\d)\\1{10}")) {
            logger.error("Invalid CPF: {}", valorChave);
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
            logger.error("Invalid CPF: {}", valorChave);
            throw new IllegalArgumentException("Invalid CPF");
        }
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        remainder = sum % 11;
        int digit2 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[10] != digit2) {
            logger.error("Invalid CPF: {}", valorChave);
            throw new IllegalArgumentException("Invalid CPF");
        }
    }

    private void validateCNPJ(String valorChave) {
        logger.info("Validating CNPJ: {}", valorChave);

        if (!isNumeric(valorChave)) {
            logger.error("CNPJ must only contain numbers: {}", valorChave);
            throw new IllegalArgumentException("The CNPJ must only contain numbers");
        }
        if (valorChave.length() != 14 || valorChave.matches("(\\d)\\1{13}")) {
            logger.error("Invalid CNPJ: {}", valorChave);
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
            logger.error("Invalid CNPJ: {}", valorChave);
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
            logger.error("Invalid CNPJ: {}", valorChave);
            throw new IllegalArgumentException("Invalid CNPJ");
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

    private void userNotFoundForId(UUID id) {
        logger.error("User not found for ID: {}", id);
        throw new EntityNotFoundException("User not found");
    }
}