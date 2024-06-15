package com.cadastro.pix.service;

import com.cadastro.pix.domain.RespDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.domain.user.dto.UserDTO;
import com.cadastro.pix.domain.user.dto.UserListDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.UserRepository;
import com.cadastro.pix.service.UserService;
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

    private User validPhysicalUserActive() {
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
        newUser.setIdentification("48428781850");
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return newUser;
    }

    //CREATE
    @Test
    public void testCreateUser_ValidUser() {
        UUID id = UUID.randomUUID();

        User newUser = validUser();

        newUser.setId(id);

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        RespDTO respDTO = userService.createUser(newUser);


        assertEquals(HttpStatus.OK, respDTO.getHttpStatus());
        assertNotNull(respDTO.getData());
        assertTrue(respDTO.getData() instanceof UserDTO);

        UserDTO userDTO = (UserDTO) respDTO.getData();
        assertEquals(id, userDTO.getId());
    }

    @Test
    public void testCreateUser_DuplicateIdentification() {
        User user = validUser();
        User duplicatedUser = validUser();

        // Simulate finding an active user with the same identification
        when(userRepository.findByIdentification(user.getIdentification()))
                .thenReturn(duplicatedUser); // Return an existing user with the same identification

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    public void testCreateUser_NameLengthMoreThen30Char() {
        User user = new User();
        user.setPersonType("fisica");
        user.setUserName("UmNomeDeCorrentistaComMaisDe30Caracteres");
        user.setUserLastName("Silva");
        user.setPhone("+5511998765432");
        user.setEmail("joao.silva@teste.com");
        user.setIdentification("48428781850");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    public void testCreateUser_EmptyName() {
        User user = new User();
        user.setPersonType("fisica");
        user.setUserName("");
        user.setUserLastName("Silva");
        user.setPhone("+5511998765432");
        user.setEmail("joao.silva@teste.com");
        user.setIdentification("48428781850");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    public void testCreateUser_LastNameMoreThen45Char() {
        User user = new User();
        user.setPersonType("fisica");
        user.setUserName("João");
        user.setUserLastName("UmSobrenomeComMaisDe45CaracteresQueDeveDarErroNaValidacaoDoSobrenome");
        user.setPhone("+5511998765432");
        user.setEmail("joao.silva@teste.com");
        user.setIdentification("48428781850");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    public void testCreateUser_PhoneWithouPlusSignal() {
        User user = new User();
        user.setPersonType("fisica");
        user.setUserName("João");
        user.setUserLastName("Silva");
        user.setPhone("11998765432"); // Phone number without +
        user.setEmail("joao.silva@teste.com");
        user.setIdentification("48428781850");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    public void testCreateUser_PhoneNumberSmallerThenExpected() {
        User user = new User();
        user.setPersonType("fisica");
        user.setUserName("João");
        user.setUserLastName("Silva");
        user.setPhone("+55119987613"); // Phone number without +
        user.setEmail("joao.silva@teste.com");
        user.setIdentification("48428781850");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    public void testCreateUser_InvalidEmail() {
        User user = new User();
        user.setPersonType("fisica");
        user.setUserName("João");
        user.setUserLastName("Silva");
        user.setPhone("+5511998765432");
        user.setEmail("email.invalido"); // Email format invalid
        user.setIdentification("48428781850");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    public void testCreateUser_InvalidEmailWithouCom() {
        User user = new User();
        user.setPersonType("fisica");
        user.setUserName("João");
        user.setUserLastName("Silva");
        user.setPhone("+5511998765432");
        user.setEmail("email@teste"); // Email format invalid
        user.setIdentification("48428781850");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    public void testCreateUser_InvalidCPF() {
        User user = new User();
        user.setPersonType("fisica");
        user.setUserName("João");
        user.setUserLastName("Silva");
        user.setPhone("+5511998765432");
        user.setEmail("joao.silva@teste.com");
        user.setIdentification("12345678901");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    public void testCreateUser_FisicaComCNPJ() {
        User user = new User();
        user.setPersonType("fisica");
        user.setUserName("João");
        user.setUserLastName("Silva");
        user.setPhone("+5511998765432");
        user.setEmail("joao.silva@teste.com");
        user.setIdentification("06947283000160");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    public void testCreateUser_JuridicaWithCPF() {
        User user = new User();
        user.setPersonType("juridica");
        user.setUserName("João");
        user.setUserLastName("Silva");
        user.setPhone("+5511998765432");
        user.setEmail("joao.silva@teste.com");
        user.setIdentification("48428781850");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    public void testCreateUser_IdentificationTooLong() {
        User user = new User();
        user.setPersonType("juridica");
        user.setUserName("João");
        user.setUserLastName("Silva");
        user.setPhone("+5511998765432");
        user.setEmail("joao.silva@teste.com");
        user.setIdentification("069472830001601");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    //UPDATE
    @Test
    public void testUpdateUser_Success() {
        UUID userId = UUID.randomUUID();
        User existingUser = validPhysicalUserActive();
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

        RespDTO response = userService.updateUser(userId, updatedUser);

        // Verify the save method is called with the updated user
        verify(userRepository).save(existingUser);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(updatedUser.getUserName(), ((UserDTO) response.getData()).getUserName());
        assertEquals(updatedUser.getUserLastName(), ((UserDTO) response.getData()).getUserLastName());
        assertEquals(updatedUser.getPhone(), ((UserDTO) response.getData()).getPhone());
        assertEquals(updatedUser.getEmail(), ((UserDTO) response.getData()).getEmail());
        assertEquals(updatedUser.getIdentification(), ((UserDTO) response.getData()).getIdentification());
    }

    @Test
    public void testUpdateUser_SuccessWithMinimalChange() {
        UUID userId = UUID.randomUUID();
        User existingUser = validPhysicalUserActive();
        existingUser.setId(userId);

        User updatedUser = new User();
        updatedUser.setPersonType("fisica");
        updatedUser.setUserName("João");
        updatedUser.setUserLastName("Silva");
        updatedUser.setPhone("+5511987654321");
        updatedUser.setEmail("joao.silva@teste.com");
        updatedUser.setIdentification("48428781850");
        updatedUser.setActive(true);

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        // Simulate saving the updated user
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        RespDTO response = userService.updateUser(userId, updatedUser);

        // Verify the save method is called with the updated user
        verify(userRepository).save(existingUser);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(updatedUser.getUserName(), ((UserDTO) response.getData()).getUserName());
        assertEquals(updatedUser.getUserLastName(), ((UserDTO) response.getData()).getUserLastName());
        assertEquals(updatedUser.getPhone(), ((UserDTO) response.getData()).getPhone());
        assertEquals(updatedUser.getEmail(), ((UserDTO) response.getData()).getEmail());
        assertEquals(updatedUser.getIdentification(), ((UserDTO) response.getData()).getIdentification());
    }

//    @Test
//    public void testUpdateUser_TryToUpdatePersonType() {
//        UUID userId = UUID.randomUUID();
//        User existingUser = validPhysicalUserActive();
//        existingUser.setId(userId);
//
//        User updatedUser = new User();
//        updatedUser.setPersonType("juridica");
//        updatedUser.setUserName("João");
//        updatedUser.setUserLastName("Silva");
//        updatedUser.setPhone("+5511987654321");
//        updatedUser.setEmail("joao.silva@teste.com");
//        updatedUser.setIdentification("06947283000160");
//        updatedUser.setActive(true);
//
//        // Simulate finding an active user
//        when(userRepository.findById(userId)).thenReturn(existingUser);
//
//        // Simulate saving the updated user
//        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
//
//        RespDTO response = userService.updateUser(userId, updatedUser);
//
//        // Verify the save method is called with the updated user
//        verify(userRepository).save(existingUser);
//
//        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, updatedUser));
//    }

    @Test
    public void testUpdateUser_UserNotFound() {
        UUID userId = UUID.randomUUID();
        User user = validUser();
        user.setId(userId);

        // Simulate user not found
        when(userRepository.findById(userId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_UserInactive() {
        UUID userId = UUID.randomUUID();
        User existingUser = validUser();
        existingUser.setActive(false);

        User user = validPhysicalUserActive();

        // Simulate finding an inactive user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_NameLengthMoreThen30Char() {
        UUID userId = UUID.randomUUID();
        User existingUser = validPhysicalUserActive();
        existingUser.setId(userId);

        User user = validPhysicalUserActive();
        user.setUserName("UmNomeDeCorrentistaComMaisDe30Caracteres");
        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_EmptyName() {
        UUID userId = UUID.randomUUID();
        User existingUser = validPhysicalUserActive();
        existingUser.setId(userId);

        User user = validPhysicalUserActive();
        user.setUserName("");

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_LastNameMoreThen45Char() {
        UUID userId = UUID.randomUUID();
        User existingUser = validPhysicalUserActive();
        existingUser.setId(userId);

        User user = validPhysicalUserActive();
        user.setUserLastName("UmSobrenomeComMaisDe45CaracteresQueDeveDarErroNaValidacaoDoSobrenome");

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_PhoneWithoutPlusSignal() {
        UUID userId = UUID.randomUUID();
        User existingUser = validPhysicalUserActive();
        existingUser.setId(userId);

        User user = validPhysicalUserActive();
        user.setPhone("11998765432"); // Phone number without +

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_PhoneNumberSmallerThenExpected() {
        UUID userId = UUID.randomUUID();
        User existingUser = validPhysicalUserActive();
        existingUser.setId(userId);

        User user = validPhysicalUserActive();
        user.setPhone("+55119987613"); // Phone number without +

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_EmailWithoutArroba() {
        UUID userId = UUID.randomUUID();
        User existingUser = validPhysicalUserActive();
        existingUser.setId(userId);

        User user = validPhysicalUserActive();
        user.setEmail("email.invalido"); // Email format invalid

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_EmailWithoutDotCom() {
        UUID userId = UUID.randomUUID();
        User existingUser = validPhysicalUserActive();
        existingUser.setId(userId);

        User user = validPhysicalUserActive();
        user.setEmail("email@teste"); // Email format invalid

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_InvalidCPF() {
        UUID userId = UUID.randomUUID();
        User existingUser = validPhysicalUserActive();
        existingUser.setId(userId);

        User user = validPhysicalUserActive();
        user.setIdentification("12345678901"); // Invalid CPF

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_FisicaComCNPJ() {
        UUID userId = UUID.randomUUID();
        User existingUser = validPhysicalUserActive();
        existingUser.setId(userId);

        User user = validPhysicalUserActive();
        user.setIdentification("06947283000160"); // Invalid CPF

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_JuridicaComCPF() {
        UUID userId = UUID.randomUUID();
        User existingUser = validLegalUserActive();
        existingUser.setId(userId);

        User user = validLegalUserActive();
        user.setIdentification("48428781850"); // Invalid CPF

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    public void testUpdateUser_IdentificationTooLong() {
        UUID userId = UUID.randomUUID();
        User existingUser = validLegalUserActive();
        existingUser.setId(userId);

        User user = validLegalUserActive();
        user.setIdentification("069472830001601");

        // Testa se a exceção correta é lançada
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    //GET
    @Test
    void testFindAllUsers() {
        List<User> users = new ArrayList<>();
        User user1 = validPhysicalUserActive();
        user1.setId(UUID.randomUUID());
        User user2 = validPhysicalUserActive();
        user2.setId(UUID.randomUUID());
        users.add(user1);
        users.add(user2);
        when(userRepository.findAll()).thenReturn(users);

        RespDTO response = userService.findAllUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertTrue(response.getData() instanceof UserListDTO);
        assertEquals(2, ((UserListDTO) response.getData()).getUsers().size());
        verify(userRepository, times(1)).findAll();
    }

//    @Test
//    void testFindUserById_Success() {
//        UUID userId = UUID.randomUUID();
//        User user = validPhysicalUserActive();
//        user.setId(userId);
//
//        when(userRepository.findById(userId)).thenReturn(user);
//
//        RespDTO response = userService.findUserById(userId);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getHttpStatus());
//        assertTrue(response.getData() instanceof UserDTO);
//        assertEquals(userId, ((UserDTO) response.getData()).getId());
//        verify(userRepository, times(1)).findById(userId);
//    }

    @Test
    void testFindUserById_NotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.findUserById(userId));

        assertEquals("Conta nao encontrada", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
    //DELETE
//    @Test
//    void testDeleteUser_Success() {
//        UUID userId = UUID.randomUUID();
//        User user = validPhysicalUserActive();
//        user.setId(userId);
//
//        when(userRepository.findById(userId)).thenReturn(user);
//
//        RespDTO response = userService.deleteUser(userId);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getHttpStatus());
//        assertTrue(response.getData() instanceof UserDTO);
//        assertFalse(((UserDTO) response.getData()).getActive());
//        verify(userRepository, times(1)).findById(userId);
//        verify(userRepository, times(1)).save(user);
//    }

    @Test
    void testDeleteUser_NotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals("User nao encontrado", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testDeleteUser_AlreadyInactive() {
        UUID userId = UUID.randomUUID();
        User user = validPhysicalUserActive();
        user.setId(userId);
        user.setActive(false);
        when(userRepository.findById(userId)).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(userId));

        assertEquals("User ja esta inativo", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
}