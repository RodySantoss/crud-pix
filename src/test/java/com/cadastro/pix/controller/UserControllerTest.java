package com.cadastro.pix.controller;

import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.user.UserDTO;
import com.cadastro.pix.dto.user.UserListDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User validUser() {
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

    @Test
    void testCreateUser_Success() {
        UUID id = UUID.randomUUID();
        User user = validUser();
        user.setId(id);

        UserDTO userDTO = new UserDTO(id);

        RespDTO respDTO = new RespDTO(HttpStatus.OK, userDTO);
        when(userService.createUser(any(User.class))).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = userController.createUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(userDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testCreateUser_IllegalArgumentException() {
        User user = new User();
        when(userService.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("User with this identification already exists and is active"));

        ResponseEntity<RespDTO> response = userController.createUser(user);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(
                "User with this identification already exists and is active",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testFindAllUsers_Success() {
        List<User> users = new ArrayList<>();
        User user1 = validUser();
        user1.setId(UUID.randomUUID());
        User user2 = validUser();
        user2.setId(UUID.randomUUID());
        users.add(user1);
        users.add(user2);
        UserListDTO usersDTO = UserListDTO.fromUsers(users);

        RespDTO respDTO = new RespDTO(HttpStatus.OK, usersDTO);

        when(userService.findAllUsers()).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = userController.findAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(usersDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testFindAllUsers_EntityNotFound() {
        when(userService.findAllUsers()).thenThrow(new EntityNotFoundException("No users found"));

        ResponseEntity<RespDTO> response = userController.findAllUsers();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No users found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testFindUserById_Success() {
        UUID id = UUID.randomUUID();
        User user = validUser();
        user.setId(id);

        UserDTO userDTO = new UserDTO(id);

        RespDTO respDTO = new RespDTO(HttpStatus.OK, userDTO);

        when(userService.findUserById(id)).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = userController.findUserById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(userDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testFindUserById_EntityNotFound() {
        UUID id = UUID.randomUUID();
        when(userService.findUserById(id)).thenThrow(new EntityNotFoundException("No users found"));

        ResponseEntity<RespDTO> response = userController.findUserById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No users found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testUpdateUser_Success() {
        UUID id = UUID.randomUUID();
        User user = validUser();

        UserDTO userDTO = new UserDTO(id);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, userDTO);

        when(userService.updateUser(eq(id), any(User.class))).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = userController.updateUser(id, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(userDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testUpdateUser_EntityNotFound() {
        UUID id = UUID.randomUUID();
        User user = validUser();
        when(userService.updateUser(eq(id), any(User.class))).thenThrow(new EntityNotFoundException("User not found"));

        ResponseEntity<RespDTO> response = userController.updateUser(id, user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testUpdateUser_IllegalArgumentException() {
        UUID id = UUID.randomUUID();
        User user = validUser();

        when(userService.updateUser(eq(id), any(User.class)))
                .thenThrow(new IllegalArgumentException("It is not possible to change the person type"));

        ResponseEntity<RespDTO> response = userController.updateUser(id, user);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(
                "It is not possible to change the person type",
                Objects.requireNonNull(response.getBody()).getMessage()
        );
    }

    @Test
    void testDeleteUser_Success() {
        UUID id = UUID.randomUUID();
        User user = validUser();

        UserDTO userDTO = new UserDTO(user);
        RespDTO respDTO = new RespDTO(HttpStatus.OK, userDTO);
        when(userService.deleteUser(id)).thenReturn(respDTO);

        ResponseEntity<RespDTO> response = userController.deleteUser(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDTO, response.getBody());
        assertEquals(userDTO, Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void testDeleteUser_EntityNotFound() {
        UUID id = UUID.randomUUID();
        when(userService.deleteUser(id)).thenThrow(new EntityNotFoundException("User not found"));

        ResponseEntity<RespDTO> response = userController.deleteUser(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testDeleteUser_IllegalArgumentException() {
        UUID id = UUID.randomUUID();
        when(userService.deleteUser(id)).thenThrow(new IllegalArgumentException("User is already inactive"));

        ResponseEntity<RespDTO> response = userController.deleteUser(id);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("User is already inactive", Objects.requireNonNull(response.getBody()).getMessage());
    }
}
