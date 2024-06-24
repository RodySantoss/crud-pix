package com.cadastro.pix.service;

import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.user.UserDTO;
import com.cadastro.pix.dto.user.UserListDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User validIndividualUserActive() {
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

    private User validLegalUserActive() {
        User newUser = new User();
        newUser.setPersonType("juridica");
        newUser.setUserName("João");
        newUser.setUserLastName("Silva");
        newUser.setPhone("+5511998765432");
        newUser.setEmail("joao.silva@teste.com");
        newUser.setIdentification("06947283000160");
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return newUser;
    }

    //CREATE
    @Test
    public void testCreateUser_ValidUser() {
        UUID id = UUID.randomUUID();

        User newUser = validIndividualUserActive();

        newUser.setId(id);

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        RespDTO respDTO = userService.createUser(newUser);

        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertNotNull(respDTO.getData());
        assertInstanceOf(UserDTO.class, respDTO.getData());

        UserDTO userDTO = (UserDTO) respDTO.getData();
        assertEquals(id, userDTO.getId());
    }

    //UPDATE
    @Test
    public void testUpdateUser_Success() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User updatedUser = new User();
        updatedUser.setPersonType("fisica");
        updatedUser.setUserName("Maria");
        updatedUser.setUserLastName("Oliveira");
        updatedUser.setPhone("+5511987654321");
        updatedUser.setEmail("maria.oliveira@teste.com");
        updatedUser.setIdentification("36216995898");
        updatedUser.setActive(true);

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        // Simulate saving the updated user
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        RespDTO respDTO = userService.updateUser(userId, updatedUser);

        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertEquals(updatedUser.getUserName(), ((UserDTO) respDTO.getData()).getUserName());
        assertEquals(updatedUser.getUserLastName(), ((UserDTO) respDTO.getData()).getUserLastName());
        assertEquals(updatedUser.getPhone(), ((UserDTO) respDTO.getData()).getPhone());
        assertEquals(updatedUser.getEmail(), ((UserDTO) respDTO.getData()).getEmail());
        assertEquals(updatedUser.getIdentification(), ((UserDTO) respDTO.getData()).getIdentification());
        assertInstanceOf(UserDTO.class, respDTO.getData());
    }

    @Test
    public void testUpdateUser_SuccessWithMinimalChange() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User updatedUser = validIndividualUserActive();
        updatedUser.setPhone("+5511987654321");
        updatedUser.setActive(true);

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        // Simulate saving the updated user
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        RespDTO respDTO = userService.updateUser(userId, updatedUser);

        // Verify the respDTO
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertEquals(updatedUser.getUserName(), ((UserDTO) respDTO.getData()).getUserName());
        assertEquals(updatedUser.getUserLastName(), ((UserDTO) respDTO.getData()).getUserLastName());
        assertEquals(updatedUser.getPhone(), ((UserDTO) respDTO.getData()).getPhone());
        assertEquals(updatedUser.getEmail(), ((UserDTO) respDTO.getData()).getEmail());
        assertEquals(updatedUser.getIdentification(), ((UserDTO) respDTO.getData()).getIdentification());
        assertInstanceOf(UserDTO.class, respDTO.getData());
    }

    //GET
    @Test
    void testFindAllUsers_Success() {
        List<User> users = new ArrayList<>();
        User user1 = validIndividualUserActive();
        user1.setId(UUID.randomUUID());
        User user2 = validIndividualUserActive();
        user2.setId(UUID.randomUUID());
        users.add(user1);
        users.add(user2);
        when(userRepository.findAll()).thenReturn(users);

        RespDTO respDTO = userService.findAllUsers();

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertEquals(2, ((UserListDTO) respDTO.getData()).getUsers().size());
        assertInstanceOf(UserListDTO.class, respDTO.getData());
    }

    @Test
    void testFindAllUsers_NotFound() {
        List<User> users = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(users);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.findAllUsers());

        assertEquals("No users found", exception.getMessage());

    }

    @Test
    void testFindUserById_Success() {
        UUID userId = UUID.randomUUID();
        User user = validIndividualUserActive();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(user);

        RespDTO respDTO = userService.findUserById(userId);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertEquals(userId, ((UserDTO) respDTO.getData()).getId());
        verify(userRepository, times(1)).findById(userId);
        assertInstanceOf(UserDTO.class, respDTO.getData());
    }

    @Test
    void testFindUserById_NotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.findUserById(userId));

        assertEquals("User not found", exception.getMessage());
    }

    // DELETE
    @Test
    void testDeleteUser_Success() {
        UUID userId = UUID.randomUUID();
        User user = validIndividualUserActive();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(user);

        RespDTO respDTO = userService.deleteUser(userId);

        assertNotNull(respDTO);
        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertInstanceOf(UserDTO.class, respDTO.getData());
        assertFalse(((UserDTO) respDTO.getData()).getActive());
    }

    @Test
    void testDeleteUser_NotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testDeleteUser_AlreadyInactive() {
        UUID userId = UUID.randomUUID();
        User user = validIndividualUserActive();
        user.setId(userId);
        user.setActive(false);
        when(userRepository.findById(userId)).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(userId));

        assertEquals("User is already inactive", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
}