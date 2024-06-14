package com.cadastro.pix.controller.service;

import com.cadastro.pix.domain.RespDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.domain.user.dto.UserDTO;
import com.cadastro.pix.repository.UserRepository;
import com.cadastro.pix.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

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

    @Test
    public void testCreateUser_ValidUser() {
        User newUser = new User();
        newUser.setUserName("Fernanda");
        newUser.setUserLastName("Mukai dos Santos");
        newUser.setIdentification("36216995898");
        newUser.setPhone("+5511976110609");
        newUser.setEmail("teste@gmail.com");
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        RespDTO respDTO = userService.createUser(newUser);

        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertNotNull(respDTO.getData());
        assertTrue(respDTO.getData() instanceof UserDTO);

        UserDTO userDTO = (UserDTO) respDTO.getData();
        assertEquals("Fernanda", userDTO.getUserName());
        assertEquals("Mukai dos Santos", userDTO.getUserLastName());
        assertEquals("36216995898", userDTO.getIdentification());
    }
}