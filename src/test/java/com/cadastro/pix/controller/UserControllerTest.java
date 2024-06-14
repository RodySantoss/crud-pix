package com.cadastro.pix.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.cadastro.pix.domain.RespDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.domain.user.dto.UserDTO;
import com.cadastro.pix.domain.user.dto.UserListDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();  // Initialize objectMapper if not using Spring Context
        mockMvc = standaloneSetup(userController).build();
    }

    @Test
    public void givenValidParams_whenCreteUser_return200() throws Exception {
        User user = new User();
        user.setUserName("Fernanda");
        user.setPersonType("fisica");
        user.setUserLastName("Mukai dos Santos");
        user.setIdentification("36216995898");
        user.setPhone("+5511976110609");
        user.setEmail("teste@gmail.com");
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        RespDTO respDTO = new RespDTO(HttpStatus.OK, new UserDTO(user));

        when(userService.createUser(any(User.class))).thenReturn(respDTO);

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("200 OK"))
                .andExpect(jsonPath("$.data.user.userName").value("Fernanda"));
    }

    @Test
    public void havingUser_whenFindAllUsers_return200() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("Fernanda");
        List<UserDTO> users = Arrays.asList(userDTO);
        UserListDTO userListDTO = new UserListDTO(users);

        RespDTO respDTO = new RespDTO(HttpStatus.OK, userListDTO);
        when(userService.findAllUsers()).thenReturn(respDTO);

        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("200 OK"))
                .andExpect(jsonPath("$.data.users[0].userName").value("Fernanda"));
    }

    @Test
    public void testFindUserById_EntityNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        // Simulando que userService.findUserById lançará EntityNotFoundException
        when(userService.findUserById(userId)).thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/api/user/" + userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value(HttpStatus.NOT_FOUND.toString()))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    public void testFindUserById() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUserName("Fernanda");
        user.setPersonType("fisica");
        user.setUserLastName("Mukai dos Santos");
        user.setIdentification("36216995898");
        user.setPhone("+5511976110609");
        user.setEmail("teste@gmail.com");
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        UserDTO userDTO = new UserDTO(user);

        RespDTO respDTO = new RespDTO(HttpStatus.OK, userDTO);
        when(userService.findUserById(userId)).thenReturn(respDTO);

        mockMvc.perform(get("/api/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("200 OK"))
                .andExpect(jsonPath("$.data.user.userName").value("Fernanda"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
//        user.setId(userId);
        user.setUserName("Fernanda");
        user.setPersonType("fisica");
        user.setUserLastName("Mukai dos Santos");
        user.setIdentification("36216995898");
        user.setPhone("+5511976110609");
        user.setEmail("teste@gmail.com");
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        RespDTO respDTO = new RespDTO(HttpStatus.OK, new UserDTO(user));
        when(userService.updateUser(any(UUID.class), any(User.class))).thenReturn(respDTO);

        mockMvc.perform(put("/api/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("200 OK"))
                .andExpect(jsonPath("$.data.user.userName").value("Fernanda"));
    }

//    @Test
//    public void testDeleteUser() throws Exception {
//        UUID userId = UUID.randomUUID();
//        RespDTO respDTO = new RespDTO(HttpStatus.OK, null);
//        when(userService.deleteUser(userId)).thenReturn(respDTO);
//
//        mockMvc.perform(delete("/api/user/" + userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.httpStatus").value("200 OK"));
//    }

    @Test
    public void testHandleValidationExceptions() throws Exception {
        User user = new User();
        user.setUserName("");

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.httpStatus").value("422 UNPROCESSABLE_ENTITY"));
    }
}
