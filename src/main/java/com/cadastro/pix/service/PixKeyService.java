package com.cadastro.pix.service;

import com.cadastro.pix.domain.*;
import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.domain.pixKey.dto.PixKeyDTO;
import com.cadastro.pix.domain.pixKey.dto.PixKeyListDTO;
import com.cadastro.pix.domain.pixKey.dto.PixKeyListWithAccountAndUserDTO;
import com.cadastro.pix.domain.pixKey.dto.PixKeyWithAccountDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.domain.user.dto.UserListWithAccountAndPixDTO;
import com.cadastro.pix.domain.user.dto.UserWithAccountsAndPixDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.PixKeyRepository;
import com.cadastro.pix.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PixKeyService {

    @Autowired
    private PixKeyRepository pixKeyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public RespDTO createPixKey(@Valid ReqObj reqObj) {
        Account account = accountRepository.findByAgencyNumberAndAccountNumber(reqObj.getAgencyNumber(), reqObj.getAccountNumber());
        if (account == null) {
            throw new EntityNotFoundException("Nao existe uma conta com essa com esse numero agencia e conta");
        }
        User user = account.getUser();
        PixKey pixKey = reqObj.toPixKey();

        List<PixKey> pixKeyList = account.getPixKeys();
        int pixKeyListSize = pixKeyList.size();

        validateCreatePixKey(pixKey, pixKeyList, account, user);

        if (user.isPhysicalPerson() && pixKeyListSize >= 5) {
            throw new IllegalArgumentException("Limite de 5 chaves por conta para Pessoa Física excedido");
        } else if (user.isLegalPerson() && pixKeyListSize >= 20) {
            throw new IllegalArgumentException("Limite de 20 chaves por conta para Pessoa Jurídica excedido");
        }

        pixKey.setActive(true); // Por padrão, nova chave PIX é ativa
        pixKey.setAccount(account);

        PixKeyDTO pixKeyDTO = new PixKeyDTO(pixKeyRepository.save(pixKey).getId());
        return new RespDTO(HttpStatus.OK, pixKeyDTO);
    }

    @Transactional
    public RespDTO deletePixKey(UUID id) {
        PixKey existingPixKey = pixKeyRepository.findById(id).orElse(null);
        if (existingPixKey == null) {
            throw new EntityNotFoundException("Chave nao encontrada");
        }

        if (!existingPixKey.isActive()) {
            throw new IllegalArgumentException("Chave ja esta inativa");
        }

        // Inativa a chave Pix e registra a data e hora da inativação
        existingPixKey.setActive(false);
        existingPixKey.setInactivatedAt(LocalDateTime.now());


        PixKeyDTO pixKeyDTO = new PixKeyDTO(pixKeyRepository.save(existingPixKey));
        return new RespDTO(HttpStatus.OK, pixKeyDTO);
    }

    @Transactional
    public RespDTO findAll() {
        List<PixKey> pixKeys = pixKeyRepository.findAll();

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    @Transactional
    public RespDTO findById(UUID id) {
        PixKey pixKey = pixKeyRepository.findById(id).orElse(null);
        if (pixKey == null) {
            throw new EntityNotFoundException("Chave nao encontrada");
        }
;
        PixKeyWithAccountDTO pixKeyDTO = new PixKeyWithAccountDTO(pixKey);
        return new RespDTO(HttpStatus.OK, pixKeyDTO);
    }

    @Transactional
    public RespDTO findByType(String keyType) {
        List<PixKey> pixKeys = pixKeyRepository.findByKeyType(keyType);
        if (pixKeys.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma chave Pix encontrada para o tipo especificado");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    @Transactional
    public RespDTO findByAgencyAndAccount(int agencyNumber, int accountNumber) {
        Account account = accountRepository.findByAgencyNumberAndAccountNumber(agencyNumber, accountNumber);
        if (account == null) {
            throw new EntityNotFoundException("Nao existe uma conta com essa com esse numero agencia e conta");
        }

        List<PixKey> pixKeys = account.getPixKeys();
        if (pixKeys.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma chave Pix encontrada para o tipo especificado");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys, account);
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    public RespDTO findByUserName(String userName) {
        List<PixKey> pixKeys = pixKeyRepository.findByUserName(userName);
        if (pixKeys.isEmpty()) {
            throw new EntityNotFoundException("Nao existe um usuario com esse nome");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    public RespDTO findByCreatedAt(LocalDate date) {
        if (date == null) {
            return new RespDTO(HttpStatus.BAD_REQUEST, "A data de inclusão deve ser fornecida para consulta");
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PixKey> pixKeys = pixKeyRepository.findByCreatedAtBetween(startOfDay, endOfDay);

        if (pixKeys.isEmpty()) {
            throw new EntityNotFoundException("Nao existe chave pix cadastrada nessa data");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }

    public RespDTO findByInactivatedAt(LocalDate date) {
        if (date == null) {
            return new RespDTO(HttpStatus.BAD_REQUEST, "A data de inclusão deve ser fornecida para consulta");
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PixKey> pixKeys = pixKeyRepository.findByInactivatedAtBetween(startOfDay, endOfDay);

        if (pixKeys.isEmpty()) {
            throw new EntityNotFoundException("Nao existe chave pix cadastrada nessa data");
        }

        PixKeyListWithAccountAndUserDTO pixKeyList = PixKeyListWithAccountAndUserDTO.fromPixKeys(pixKeys);
        return new RespDTO(HttpStatus.OK, pixKeyList);
    }
//
//    public ResponseEntity<?> getPixKeysByInativacao(LocalDate dataInativacao) {
//        // Verifica se foi fornecida a data de inativação
//        if (dataInativacao == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inativação deve ser fornecida para consulta");
//        }
//
//        // Consulta no repositório pixKeyRepository
//        List<PixKey> pixKeys = pixKeyRepository.findByInactivatedAt(dataInativacao);
//
//        // Verifica se foram encontradas chaves Pix para a data de inativação fornecida
//        if (!pixKeys.isEmpty()) {
//            return ResponseEntity.ok(pixKeys);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma chave Pix encontrada para a data de inativação especificada");
//        }
//    }

    private void validateCreatePixKey(PixKey pixKey, List<PixKey> pixKeyList, Account account, User user) {
        String keyValue = pixKey.getKeyValue();
        if (pixKeyRepository.existsByKeyValue(keyValue)) {
            throw new IllegalArgumentException("Valor de chave já cadastrado");
        }

        // Validação adicional baseada no tipo de chave
        switch (pixKey.getKeyType().toLowerCase()) {
            case "celular":
                validateCelular(keyValue);
                break;
            case "email":
                validateEmail(keyValue);
                break;
            case "cpf":
                if(user.isLegalPerson())
                    throw new IllegalArgumentException("Pessoa juridica nao pode cadastrar chave CPF");

                validateCPF(keyValue, pixKeyList, account);
                break;
            case "cnpj":
                if(user.isPhysicalPerson())
                    throw new IllegalArgumentException("Pessoa fisica nao pode cadastrar chave CNPJ");

                validateCNPJ(keyValue, pixKeyList, account);
                break;
            case "aleatorio":
                validateChaveAleatoria(keyValue);
                break;
            default:
                throw new IllegalArgumentException("Tipo de chave inválido");
        }
    }

    private void validateCelular(String valorChave) {
        if (!valorChave.matches("^\\+\\d{1,2}\\d{1,3}\\d{9}$")) {
            throw new IllegalArgumentException("Formato de telefone inválido");
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

    private void validateCPF(String valorChave, List<PixKey> pixKeyList, Account account) {
        if(!account.getUser().getIdentification().equals(valorChave)) {
            throw new IllegalArgumentException("A chave CPF deve ser igual o CPF da conta");
        }

        for (PixKey pixKey : pixKeyList) {
            if(pixKey.getKeyType().equalsIgnoreCase("cpf")) {
                throw new IllegalArgumentException("Chave CPF ja cadastrada para essa conta.");
            }
        }

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

    private void validateCNPJ(String valorChave, List<PixKey> pixKeyList, Account account) {
        if(!account.getUser().getIdentification().equals(valorChave)) {
            throw new IllegalArgumentException("A chave CNPJ deve ser igual o CNPJ da conta");
        }

        for (PixKey pixKey : pixKeyList) {
            if(pixKey.getKeyType().equalsIgnoreCase("cnpj")) {
                throw new IllegalArgumentException("Chave CNPJ ja cadastrada para essa conta.");
            }
        }

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

    private void validateChaveAleatoria(String valorChave) {
        if (!valorChave.matches("[a-zA-Z0-9]{36}")) {
            throw new IllegalArgumentException("Chave aleatória inválida");
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