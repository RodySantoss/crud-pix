package com.cadastro.pix.service;

import com.cadastro.pix.domain.RespDTO;
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

    @Test
    public void testCreateUser_DuplicateIdentification() {
        User user = validIndividualUserActive();

        when(userRepository.findByIdentification(user.getIdentification()))
                .thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("User with this identification already exists and is active", exception.getMessage());
    }

    @Test
    public void testCreateUser_DuplicateIdentificationInactive() {
        User user = validIndividualUserActive();
        user.setActive(false);

        when(userRepository.findByIdentification(user.getIdentification()))
                .thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("User with this identification already exists but is inactive", exception.getMessage());
    }

    @Test
    public void testCreateUser_NullName() {
        User user = validIndividualUserActive();
        user.setUserName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testCreateUser_EmptyName() {
        User user = validIndividualUserActive();
        user.setUserName("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testCreateUser_NameLengthMoreThen30Char() {
        User user = validIndividualUserActive();
        user.setUserName("UmNomeDeCorrentistaComMaisDe30Caracteres");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testCreateUser_LastNameMoreThen45Char() {
        User user = validIndividualUserActive();
        user.setUserLastName("UmSobrenomeComMaisDe45CaracteresQueDeveDarErroNaValidacaoDoSobrenome");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid account holder last name", exception.getMessage());
    }

    @Test
    public void testCreateUser_PhoneWithoutPlusSignal() {
        User user = validIndividualUserActive();
        user.setPhone("5511998765432"); // Phone number without +

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testCreateUser_PhoneNumberSmallerThenExpected() {
        User user = validIndividualUserActive();
        user.setPhone("+5511987654"); // Phone number smaller

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testCreateUser_PhoneNumberBiggerThenExpected() {
        User user = validIndividualUserActive();
        user.setPhone("+551198765432100"); // Phone number bigger

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testCreateUser_EmailWithoutArroba() {
        User user = validIndividualUserActive();
        user.setEmail("email.invalido"); // Email format invalid

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testCreateUser_InvalidEmailWithouCom() {
        User user = validIndividualUserActive();
        user.setEmail("email@teste"); // Email format invalid

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testCreateUser_InvalidEmailBiggerThen77Char() {
        User user = validIndividualUserActive();
        user.setEmail("emailaasdadasdassadassasddsadsdadsadsasddsaadsadssdadssdadaaaaasaaas@teste.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testCreateUser_CPFAlphanumeric() {
        User user = validIndividualUserActive();
        user.setIdentification("484287818as");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(user));

        assertEquals("The CPF must only contain numbers", exception.getMessage());
    }

    @Test
    public void testCreateUser_CPFSmaller() {
        User user = validIndividualUserActive();
        user.setIdentification("4842878185"); // CPF Smaller

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testCreateUser_CPFBigger() {
        User user = validIndividualUserActive();
        user.setIdentification("484287818501"); // CPF Bigger

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testCreateUser_IndividualWithCNPJ() {
        User user = validIndividualUserActive();
        user.setIdentification("06947283000160"); // CNPJ

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testCreateUser_CNPJAlphanumeric() {
        User user = validLegalUserActive();
        user.setIdentification("0694728300AS60");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(user));

        assertEquals("The CNPJ must only contain numbers", exception.getMessage());
    }

    @Test
    public void testCreateUser_CNPJSmaller() {
        User user = validLegalUserActive();
        user.setIdentification("0694728300016"); // CNPJ Smaller

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    public void testCreateUser_CNPJBigger() {
        User user = validLegalUserActive();
        user.setIdentification("069472830001601"); // CNPJ Bigger

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    public void testCreateUser_LegalWithCPF() {
        User user = validLegalUserActive();
        user.setIdentification("48428781850"); // CPF

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Invalid CNPJ", exception.getMessage());
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

    @Test
    public void testUpdateUser_UserNotFound() {
        UUID userId = UUID.randomUUID();
        User user = validIndividualUserActive();

        // Simulate user not found
        when(userRepository.findById(userId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testUpdateUser_UserInactive() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setActive(false);

        User user = validIndividualUserActive();

        // Simulate finding an inactive user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("User is inactive", exception.getMessage());
    }

    @Test
    public void testUpdateUser_TryToUpdatePersonType() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User updatedUser = validIndividualUserActive();
        updatedUser.setPersonType("juridica");

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        // Simulate saving the updated user
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, updatedUser);
        });

        assertEquals("It is not possible to change the person type", exception.getMessage());
    }

    @Test
    public void testUpdateUser_NullName() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setUserName("");

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testUpdateUser_EmptyName() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setUserName("");

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testUpdateUser_NameLengthMoreThen30Char() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setUserName("UmNomeDeCorrentistaComMaisDe30Caracteres");
        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testUpdateUser_LastNameMoreThen45Char() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setUserLastName("UmSobrenomeComMaisDe45CaracteresQueDeveDarErroNaValidacaoDoSobrenome");

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("Invalid account holder last name", exception.getMessage());
    }

    @Test
    public void testUpdateUser_PhoneWithoutPlusSignal() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setPhone("5511998765432"); // Phone number without +

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testUpdateUser_PhoneNumberSmallerThenExpected() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setPhone("+5511987654"); // Phone smaller

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testUpdateUser_PhoneNumberBiggerThenExpected() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setPhone("+551198765432100"); // Phone bigger

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testUpdateUser_EmailWithoutArroba() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setEmail("email.invalido"); // Email format invalid

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testUpdateUser_EmailWithoutDotCom() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setEmail("email@teste"); // Email format invalid

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testUpdateUser_EmailBiggerThen77Char() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setEmail("emailaasdadasdassadassasddsadsdadsadsasddsaadsadssdadssdadaaaaasaaas@teste.com");

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testUpdateUser_CPFAlphanumeric() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setIdentification("484287818as"); // Invalid CPF

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userId, user));

        assertEquals("The CPF must only contain numbers", exception.getMessage());
    }

    @Test
    public void testUpdateUser_CPFSmaller() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setIdentification("4842878185"); // Invalid CPF

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userId, user));

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testUpdateUser_CPFBigger() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setIdentification("484287818501"); // Invalid CPF

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userId, user));

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testUpdateUser_IndividualComCNPJ() {
        UUID userId = UUID.randomUUID();
        User existingUser = validIndividualUserActive();
        existingUser.setId(userId);

        User user = validIndividualUserActive();
        user.setIdentification("06947283000160"); // CNPJ

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userId, user));

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testUpdateUser_CNPJAlphanumeric() {
        UUID userId = UUID.randomUUID();
        User existingUser = validLegalUserActive();
        existingUser.setId(userId);

        User user = validLegalUserActive();
        user.setIdentification("069472830asd60"); // Invalid CNPJ

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userId, user));

        assertEquals("The CNPJ must only contain numbers", exception.getMessage());
    }

    @Test
    public void testUpdateUser_CNPJSmaller() {
        UUID userId = UUID.randomUUID();
        User existingUser = validLegalUserActive();
        existingUser.setId(userId);

        User user = validLegalUserActive();
        user.setIdentification("0694728300016"); // Invalid CNPJ

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userId, user));

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    public void testUpdateUser_CNPJBigger() {
        UUID userId = UUID.randomUUID();
        User existingUser = validLegalUserActive();
        existingUser.setId(userId);

        User user = validLegalUserActive();
        user.setIdentification("069472830001601"); // Invalid CNPJ

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userId, user));

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    public void testUpdateUser_LegalWithCPF() {
        UUID userId = UUID.randomUUID();
        User existingUser = validLegalUserActive();
        existingUser.setId(userId);

        User user = validLegalUserActive();
        user.setIdentification("48428781850"); // Invalid CPF

        // Simulate finding an active user
        when(userRepository.findById(userId)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userId, user));

        assertEquals("Invalid CNPJ", exception.getMessage());
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