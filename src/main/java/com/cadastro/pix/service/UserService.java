package com.cadastro.pix.service;

import com.cadastro.pix.domain.Account;
import com.cadastro.pix.domain.User;
import com.cadastro.pix.domain.User;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User createUser(@Valid User user) {
        validateCreateUser(user);

        user.setAtivo(true);

        return userRepository.save(user);
    }

    public static UUID bytesToUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }

    @Transactional
    public User updateUser(UUID id, User user) {
        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            throw new EntityNotFoundException("Conta nao encontrada");
        }

        if (!existingUser.isAtivo()) {
            throw new IllegalArgumentException("Usuario inativo");
        }
        
        validateUpdatedFields(user);
        
        existingUser.setNomeCorrentista(user.getNomeCorrentista());
        existingUser.setSobrenomeCorrentista(user.getSobrenomeCorrentista());
        existingUser.setCelular(user.getCelular());
        existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    private void validateCreateUser(User user) {
        validateExistUser(user);
        validateNomeCorrentista(user.getNomeCorrentista());
        validateSobrenomeCorrentista(user.getSobrenomeCorrentista());
        validateCelular(user.getCelular());
        validateEmail(user.getEmail());
        validateIdentificacao(user);
    }

    private void validateUpdatedFields(User user) {
        validateNomeCorrentista(user.getNomeCorrentista());
        validateSobrenomeCorrentista(user.getSobrenomeCorrentista());
        validateCelular(user.getCelular());
        validateEmail(user.getEmail());
        validateIdentificacao(user);
    }

    private void validateExistUser(User user) {
        User existUser = userRepository.findByIdentificacao(user.getIdentificacao());
        if (existUser != null) {
            if(existUser.isAtivo()) throw new IllegalArgumentException("Ja existe um usuario com esta identificaçao");

            throw new IllegalArgumentException("Ja existe um usuario inativo com esta identificaçao");
        }
    }

    private void validateNomeCorrentista(String nome) {
        if (nome == null || nome.length() > 30) {
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
        if (!valorChave.contains("@") || valorChave.length() > 77) {
            throw new IllegalArgumentException("E-mail inválido");
        }
    }

    private void validateIdentificacao(User user) {
        if(user.isPessoaFisica()) {
            validateCPF(user.getIdentificacao());
        } else if(user.isPessoaJuridica()) {
            validateCNPJ(user.getIdentificacao());
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
        valorChave = valorChave.replaceAll("[^0-9]", "");
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