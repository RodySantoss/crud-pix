package com.cadastro.pix.utils;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.account.CreateAccountDTO;
import com.cadastro.pix.dto.pixKey.CreatePixKeyDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.PixKeyRepository;
import com.cadastro.pix.repository.UserRepository;
import com.cadastro.pix.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class ValidateTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PixKeyRepository pixKeyRepository;

    @InjectMocks
    private Validate validate;

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

    private Account validAccount() {
        Account validAccount = new Account();
        validAccount.setId(UUID.randomUUID());
        validAccount.setAccountType("corrente");
        validAccount.setAgencyNumber(1234);
        validAccount.setAccountNumber(56789012);
        validAccount.setUser(validIndividualUserActive());
        validAccount.setActive(true);

        return validAccount;
    }

    private Account validLegalAccount() {
        Account validAccount = new Account();
        validAccount.setId(UUID.randomUUID());
        validAccount.setAccountType("corrente");
        validAccount.setAgencyNumber(1234);
        validAccount.setAccountNumber(56789012);
        validAccount.setUser(validLegalUserActive());
        validAccount.setActive(true);

        return validAccount;
    }

    private PixKey validPixKey() {
        PixKey validPixKey = new PixKey();
        validPixKey.setId(UUID.randomUUID());
        validPixKey.setKeyType("cpf");
        validPixKey.setKeyValue("48428781850");
        validPixKey.setAccount(validAccount());
        validPixKey.setActive(true);

        return validPixKey;
    }

    private PixKey validLegalPixKey() {
        PixKey validPixKey = new PixKey();
        validPixKey.setId(UUID.randomUUID());
        validPixKey.setKeyType("cnpj");
        validPixKey.setKeyValue("06947283000160");
        validPixKey.setAccount(validLegalAccount());
        validPixKey.setActive(true);

        return validPixKey;
    }

    //VALIDATE UPDATE USER
    @Test
    public void testValidateCreateUser_ExistUser() {
        User user = validIndividualUserActive();

        when(userRepository.findByIdentification(user.getIdentification()))
                .thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("User with this identification already exists and is active", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_ExistUserInactive() {
        User user = validIndividualUserActive();
        user.setActive(false);

        when(userRepository.findByIdentification(user.getIdentification()))
                .thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("User with this identification already exists but is inactive", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_NullName() {
        User user = validIndividualUserActive();
        user.setUserName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_EmptyName() {
        User user = validIndividualUserActive();
        user.setUserName("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_NameLengthMoreThen30Char() {
        User user = validIndividualUserActive();
        user.setUserName("UmNomeDeCorrentistaComMaisDe30Caracteres");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_LastNameMoreThen45Char() {
        User user = validIndividualUserActive();
        user.setUserLastName("UmSobrenomeComMaisDe45CaracteresQueDeveDarErroNaValidacaoDoSobrenome");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid account holder last name", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_PhoneWithoutPlusSignal() {
        User user = validIndividualUserActive();
        user.setPhone("5511998765432"); // Phone number without +

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_PhoneNumberSmallerThenExpected() {
        User user = validIndividualUserActive();
        user.setPhone("+5511987654"); // Phone number smaller

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {

            validate.validateCreateUser(user);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_PhoneNumberBiggerThenExpected() {
        User user = validIndividualUserActive();
        user.setPhone("+551198765432100"); // Phone number bigger

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_EmailWithoutArroba() {
        User user = validIndividualUserActive();
        user.setEmail("email.invalido"); // Email format invalid

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_InvalidEmailWithouCom() {
        User user = validIndividualUserActive();
        user.setEmail("email@teste"); // Email format invalid

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_InvalidEmailBiggerThen77Char() {
        User user = validIndividualUserActive();
        user.setEmail("emailaasdadasdassadassasddsadsdadsadsasddsaadsadssdadssdadaaaaasaaas@teste.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_CPFAlphanumeric() {
        User user = validIndividualUserActive();
        user.setIdentification("484287818as");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("The CPF must only contain numbers", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_CPFSmaller() {
        User user = validIndividualUserActive();
        user.setIdentification("4842878185"); // CPF Smaller

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_CPFBigger() {
        User user = validIndividualUserActive();
        user.setIdentification("484287818501"); // CPF Bigger

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_IndividualWithCNPJ() {
        User user = validIndividualUserActive();
        user.setIdentification("06947283000160"); // CNPJ

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_CNPJAlphanumeric() {
        User user = validLegalUserActive();
        user.setIdentification("0694728300AS60");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("The CNPJ must only contain numbers", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_CNPJSmaller() {
        User user = validLegalUserActive();
        user.setIdentification("0694728300016"); // CNPJ Smaller

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_CNPJBigger() {
        User user = validLegalUserActive();
        user.setIdentification("069472830001601"); // CNPJ Bigger

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    public void testValidateCreateUser_LegalWithCPF() {
        User user = validLegalUserActive();
        user.setIdentification("48428781850"); // CPF

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateUser(user);
        });

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    //VALIDATE UPDATE USER
    @Test
    public void testValidateUpdateUser_TryToUpdatePersonType() {
        User user = validIndividualUserActive();
        String existingUserType = "juridica";

        // Simulate saving the updated user
//        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("It is not possible to change the person type", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_NullName() {
        User user = validIndividualUserActive();
        user.setUserName(null);
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_EmptyName() {
        User user = validIndividualUserActive();
        user.setUserName("");
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_NameLengthMoreThen30Char() {
        User user = validIndividualUserActive();
        user.setUserName("UmNomeDeCorrentistaComMaisDe30Caracteres");
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid account holder name", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_LastNameMoreThen45Char() {
        User user = validIndividualUserActive();
        user.setUserLastName("UmSobrenomeComMaisDe45CaracteresQueDeveDarErroNaValidacaoDoSobrenome");
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid account holder last name", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_PhoneWithoutPlusSignal() {
        User user = validIndividualUserActive();
        user.setPhone("5511998765432"); // Phone number without +
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_PhoneNumberSmallerThenExpected() {
        User user = validIndividualUserActive();
        user.setPhone("+5511987654"); // Phone smaller
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_PhoneNumberBiggerThenExpected() {
        User user = validIndividualUserActive();
        user.setPhone("+551198765432100"); // Phone bigger
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_EmailWithoutArroba() {
        User user = validIndividualUserActive();
        user.setEmail("email.invalido"); // Email format invalid
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_EmailWithoutDotCom() {
        User user = validIndividualUserActive();
        user.setEmail("email@teste"); // Email format invalid
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_EmailBiggerThen77Char() {
        User user = validIndividualUserActive();
        user.setEmail("emailaasdadasdassadassasddsadsdadsadsasddsaadsadssdadssdadaaaaasaaas@teste.com");
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_CPFAlphanumeric() {
        User user = validIndividualUserActive();
        user.setIdentification("484287818as"); // Invalid CPF
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("The CPF must only contain numbers", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_CPFSmaller() {
        User user = validIndividualUserActive();
        user.setIdentification("4842878185"); // Invalid CPF
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_CPFBigger() {
        User user = validIndividualUserActive();
        user.setIdentification("484287818501"); // Invalid CPF
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_IndividualComCNPJ() {
        User user = validIndividualUserActive();
        user.setIdentification("06947283000160"); // CNPJ
        String existingUserType = "fisica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_CNPJAlphanumeric() {
        User user = validLegalUserActive();
        user.setIdentification("069472830asd60"); // Invalid CNPJ
        String existingUserType = "juridica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("The CNPJ must only contain numbers", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_CNPJSmaller() {
        User user = validLegalUserActive();
        user.setIdentification("0694728300016"); // Invalid CNPJ
        String existingUserType = "juridica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_CNPJBigger() {
        User user = validLegalUserActive();
        user.setIdentification("069472830001601"); // Invalid CNPJ
        String existingUserType = "juridica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    public void testValidateUpdateUser_LegalWithCPF() {
        User user = validLegalUserActive();
        user.setIdentification("48428781850"); // Invalid CPF
        String existingUserType = "juridica";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateUpdateUser(user, existingUserType);
        });

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    // VALIDATE CREATE ACCOUNT
    @Test
    void testValidateCreateAccount_ExistingAccount() {
        Account account = validAccount();

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(account);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });

        assertEquals("There is already an account with that account number at this agency", exception.getMessage());
    }

    @Test
    void testValidateCreateAccount_ExistingAccountInactivated() {
        Account account = validAccount();
        account.setActive(false);

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(account);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });

        assertEquals("There is already an inactive account with that account number at this agency", exception.getMessage());
    }

    @Test
    void testValidateCreateAccount_InvalidAccountType() {
        Account account = validAccount();
        account.setAccountType("invalid");

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });

        assertEquals("Invalid account type", exception.getMessage());
    }

    @Test
    void testValidateCreateAccount_NullAgencyNumber() {
        Account account = validAccount();
        account.setAgencyNumber(null);

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });

        assertEquals("Invalid agency number", exception.getMessage());
    }

    @Test
    void testValidateCreateAccount_BiggerAgencyNumber() {
        Account account = validAccount();
        account.setAgencyNumber(12345);

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });

        assertEquals("Invalid agency number", exception.getMessage());
    }

    @Test
    void testValidateCreateAccount_NullAccountNumber() {
        Account account = validAccount();
        account.setAccountNumber(null);

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });

        assertEquals("Invalid account number", exception.getMessage());
    }

    @Test
    void testValidateCreateAccount_BiggerAccountNumber() {
        Account account = validAccount();
        account.setAccountNumber(123456789);

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });


        assertEquals("Invalid account number", exception.getMessage());
    }

    @Test
    void testValidateUpdateAccount_InvalidAccountType() {
        Account account = validAccount();
        account.setAccountType("invalid");

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });

        assertEquals("Invalid account type", exception.getMessage());
    }

    @Test
    void testValidateUpdateAccount_NullAgencyNumber() {
        Account account = validAccount();
        account.setAgencyNumber(null);

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });

        assertEquals("Invalid agency number", exception.getMessage());
    }

    @Test
    void testValidateUpdateAccount_BiggerAgencyNumber() {
        Account account = validAccount();
        account.setAgencyNumber(12345);

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });

        assertEquals("Invalid agency number", exception.getMessage());
    }

    @Test
    void testValidateUpdateAccount_NullAccountNumber() {
        Account account = validAccount();
        account.setAccountNumber(null);

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });

        assertEquals("Invalid account number", exception.getMessage());
    }

    @Test
    void testValidateUpdateAccount_BiggerAccountNumber() {
        Account account = validAccount();
        account.setAccountNumber(123456789);

        when(accountRepository.findByAgencyNumberAndAccountNumber(any(Integer.class), any(Integer.class)))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validate.validateCreateAccount(account);
        });


        assertEquals("Invalid account number", exception.getMessage());
    }

    //PIX KEY
    @Test
    void testValidateCreatePixKey_KeyValueExists() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Pix key value already registered", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_PhoneFormatWithouPlusSignal() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("celular");
        pixKey.setKeyValue("5511976110609");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_PhoneNumberSmallerThenExpected() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("celular");
        pixKey.setKeyValue("+551198765");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_PhoneNumberBiggerThenExpected() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("celular");
        pixKey.setKeyValue("551198765432101");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid phone format", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_EmailWithoutArroba() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("email");
        pixKey.setKeyValue("teste.teste.com");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_EmailWithoutDotPlusText() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("email");
        pixKey.setKeyValue("teste@teste");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CannotRegisterCNPJKey() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("cnpj");
        pixKey.setKeyValue("06947283000160");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Individuals cannot register a CNPJ key", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CPFDifferentThenUserCPF() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("cpf");
        pixKey.setKeyValue("36216995898");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("The CPF key must be the same as the account's CPF", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CPFKeyAlreadyExist() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKeys.add(pixKey);

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("CPF key already registered for this account", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CPFKeyAlphanumeric() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("cpf");
        pixKey.setKeyValue("484287818as");
        user.setIdentification("484287818as");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("The CPF must only contain numbers", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CPFKeySmaller() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("cpf");
        pixKey.setKeyValue("4842878185");
        user.setIdentification("4842878185");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_IndividualPerson_CPFKeyBigger() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("cpf");
        pixKey.setKeyValue("484287818501");
        user.setIdentification("484287818501");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CannotRegisterCPFKey() {
        PixKey pixKey = validLegalPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("cpf");
        pixKey.setKeyValue("48428781850");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Legal entities cannot register a CPF key", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CNPJDifferentThenUser() {
        PixKey pixKey = validLegalPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("cnpj");
        pixKey.setKeyValue("09188942000110");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("The CNPJ key must be the same as the account's CNPJ", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CNPJKeyAlreadyExist() {
        PixKey pixKey = validLegalPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("cnpj");
        pixKey.setKeyValue("06947283000160");

        pixKeys.add(pixKey);

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("CNPJ key already registered for this account", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CNPJKeyAlphanumeric() {
        PixKey pixKey = validLegalPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        user.setIdentification("069472830001as");

        pixKey.setKeyType("cnpj");
        pixKey.setKeyValue("069472830001as");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("The CNPJ must only contain numbers", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CNPJKeySmaller() {
        PixKey pixKey = validLegalPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        user.setIdentification("0694728300016");

        pixKey.setKeyType("cnpj");
        pixKey.setKeyValue("0694728300016");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_LegalPerson_CNPJKeyBigger() {
        PixKey pixKey = validLegalPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        user.setIdentification("069472830001601");

        pixKey.setKeyType("cnpj");
        pixKey.setKeyValue("069472830001601");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_InvalidRandomKey() {
        PixKey pixKey = validLegalPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("aleatorio");
        pixKey.setKeyValue("550e8400-e29b-41d4-a716-44665544000G");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid random key", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_RandomKeySmallerThen36Char() {
        PixKey pixKey = validLegalPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("aleatorio");
        pixKey.setKeyValue("550e8400-e29b-41d4-a716-44665544000");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));
        assertEquals("Invalid random key", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_RandomKeyBiggerThen36Char() {
        PixKey pixKey = validLegalPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("aleatorio");
        pixKey.setKeyValue("550e8400-e29b-41d4-a716-446655440007a");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid random key", exception.getMessage());
    }

    @Test
    void testValidateCreatePixKey_InvalidKeyType() {
        PixKey pixKey = validLegalPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        pixKey.setKeyType("invalid");

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Invalid key type", exception.getMessage());
    }

    @Test
    void testCreatePixKey_IndividualPersonLimitExceeded() {
        PixKey pixKey = validPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        for (int i = 0; i < 5; i++) {
            PixKey existingPixKey = validPixKey();
            existingPixKey.setKeyType("aleatorio");
            existingPixKey.setKeyValue("3700040e-eb6a-44d9-8361-406f622d26a5");
            pixKeys.add(existingPixKey);
        }

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Limit of 5 keys per account for Individuals exceeded", exception.getMessage());
    }

    @Test
    void testCreatePixKey_LegalPersonLimitExceeded() {
        PixKey pixKey = validLegalPixKey();
        List<PixKey> pixKeys = new ArrayList<>();
        Account account = pixKey.getAccount();
        User user = account.getUser();

        for (int i = 0; i < 20; i++) {
            PixKey existingPixKey = validLegalPixKey();
            existingPixKey.setKeyType("aleatorio");
            existingPixKey.setKeyValue("3700040e-eb6a-44d9-8361-406f622d26a5");
            pixKeys.add(existingPixKey);
        }

        when(pixKeyRepository.existsByKeyValueAndActive(anyString(), anyBoolean())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                validate.validateCreatePixKey(pixKey, pixKeys, account, user));

        assertEquals("Limit of 20 keys per account for Legal Entities exceeded", exception.getMessage());
    }
}
