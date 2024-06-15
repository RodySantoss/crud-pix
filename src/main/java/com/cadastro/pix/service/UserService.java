package com.cadastro.pix.service;

import com.cadastro.pix.domain.RespDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.domain.user.dto.UserDTO;
import com.cadastro.pix.domain.user.dto.UserListDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public RespDTO createUser(@Valid User user) {
        validateCreateUser(user);

        user.setActive(true);

        UserDTO userDTO = new UserDTO(userRepository.save(user).getId());
        return new RespDTO(HttpStatus.OK, userDTO);
    }

    public RespDTO findAllUsers() {
        List<User> users = userRepository.findAll();
        UserListDTO usersDTO = UserListDTO.fromUsers(users);

        return new RespDTO(HttpStatus.OK, usersDTO);
    }

    public RespDTO findUserById(UUID id) {
        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            throw new EntityNotFoundException("Conta nao encontrada");
        }

        UserDTO userDTO = new UserDTO(userRepository.save(existingUser));
        return new RespDTO(HttpStatus.OK, userDTO);
    }

    @Transactional
    public RespDTO updateUser(UUID id, User user) {
        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            throw new EntityNotFoundException("Usuario nao encontrado");
        }

        if (!existingUser.isActive()) {
            throw new IllegalArgumentException("Usuario inativo");
        }
        
        validateUpdatedFields(user, existingUser.getPersonType());
        
        existingUser.setUserName(user.getUserName());
        existingUser.setUserLastName(user.getUserLastName());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());

        //updatedAt so vem atualizado na segunda chamada
        User updatedUser = userRepository.save(existingUser);
        UserDTO userDTO = new UserDTO(updatedUser);
        return new RespDTO(HttpStatus.OK, userDTO);
    }

    @Transactional
    public RespDTO deleteUser(UUID id) {
        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            throw new EntityNotFoundException("User nao encontrado");
        }

        if (!existingUser.isActive()) {
            throw new IllegalArgumentException("User ja esta inativo");
        }

        // Inativa a chave Pix e registra a data e hora da inativação
        existingUser.setActive(false);
        existingUser.setInactivatedAt(LocalDateTime.now());

        UserDTO userDTO = new UserDTO(userRepository.save(existingUser));
        return new RespDTO(HttpStatus.OK, userDTO);
    }
    
    private void validateCreateUser(User user) {
        validateExistUser(user);
        validateNomeCorrentista(user.getUserName());
        validateSobrenomeCorrentista(user.getUserLastName());
        validateCelular(user.getPhone());
        validateEmail(user.getEmail());
        validateIdentificacao(user);
    }

    private void validateUpdatedFields(User user, String existingUserType) {
        validateUserType(user.getPersonType(), existingUserType);
        validateNomeCorrentista(user.getUserName());
        validateSobrenomeCorrentista(user.getUserLastName());
        validateCelular(user.getPhone());
        validateEmail(user.getEmail());
        validateIdentificacao(user);
    }

    private void validateExistUser(User user) {
        User existUser = userRepository.findByIdentification(user.getIdentification());
        if (existUser != null) {
            if(existUser.isActive()) throw new IllegalArgumentException("Ja existe um usuario com esta identificaçao");

            throw new IllegalArgumentException("Ja existe um usuario inativo com esta identificaçao");
        }
    }

    private void validateUserType(String existingUserType, String newUserType) {
        if (existingUserType != newUserType) {
            throw new IllegalArgumentException("Nao e possivel trocar o tipo de pessoa");
        }
    }

    private void validateNomeCorrentista(String nome) {
        if (nome == null || nome.isEmpty()|| nome.length() > 30) {
            throw new IllegalArgumentException("Nome do correntista inválido");
        }
    }

    private void validateSobrenomeCorrentista(String sobrenome) {
        if (sobrenome.length() > 45) {
            throw new IllegalArgumentException("Sobrenome do correntista inválido");
        }
    }

    private void validateCelular(String valorChave) {
        if (!valorChave.matches("^\\+\\d{1,3}\\d{11}$")) {
            throw new IllegalArgumentException("Celular inválido");
        }
    }

    private void validateEmail(String valorChave) {
        // Deve ter o modelo "texto@texto.texto"
        final String emailRegexPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        Pattern pattern = Pattern.compile(emailRegexPattern);
        Matcher matcher = pattern.matcher(valorChave);

        if(!matcher.matches() || valorChave.length() > 77){
            throw new IllegalArgumentException("E-mail inválido");
        }
    }

    private void validateIdentificacao(User user) {
        if(user.isPhysicalPerson()) {
            validateCPF(user.getIdentification());
        } else if(user.isLegalPerson()) {
            validateCNPJ(user.getIdentification());
        } else {
            throw new IllegalArgumentException("Tipo de pessoa inválido");
        }
    }

    private void validateCPF(String valorChave) {
        if (!isNumeric(valorChave)) {
            throw new IllegalArgumentException("O CPF deve conter apenas números");
        }
        if (valorChave.length() != 11 || valorChave.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("CPF inválido");
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
            throw new IllegalArgumentException("CPF inválido");
        }
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        remainder = sum % 11;
        int digit2 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[10] != digit2) {
            throw new IllegalArgumentException("CPF inválido");
        }
    }

    private void validateCNPJ(String valorChave) {
        if (!isNumeric(valorChave)) {
            throw new IllegalArgumentException("O CNPJ deve conter apenas números");
        }
        if (valorChave.length() != 14 || valorChave.matches("(\\d)\\1{13}")) {
            throw new IllegalArgumentException("CNPJ inválido");
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
            throw new IllegalArgumentException("CNPJ inválido");
        }
        sum = 0;
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 13; i++) {
            sum += digits[i] * weights2[i];
        }
        remainder = sum % 11;
        int digit2 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[13] != digit2) {
            throw new IllegalArgumentException("CNPJ inválido");
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
            return false;
        }
    }
}