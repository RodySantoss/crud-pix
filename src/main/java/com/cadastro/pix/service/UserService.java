package com.cadastro.pix.service;

import com.cadastro.pix.controller.UserController;
import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.user.UserDTO;
import com.cadastro.pix.dto.user.UserListDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.UserRepository;
import com.cadastro.pix.utils.Validate;
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

    @Autowired
    private Validate validate;

    @Transactional
    public RespDTO createUser(@Valid User user) {
        logger.info("Starting user creation process for user: {}", user);

        validate.validateCreateUser(user);
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

        validate.validateUpdateUser(user, existingUser.getPersonType());

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

    private void userNotFoundForId(UUID id) {
        logger.error("User not found for ID: {}", id);
        throw new EntityNotFoundException("User not found");
    }
}