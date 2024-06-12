package com.cadastro.pix.service;

import com.cadastro.pix.domain.*;
import com.cadastro.pix.repository.AccountRepository;
import com.cadastro.pix.repository.PixKeyRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PixKeyService {

    @Autowired
    private PixKeyRepository pixKeyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public PixKey createPixKey(@Valid ReqObj reqObj) {
        Account account = accountRepository.findByNumeroAgenciaAndNumeroConta(reqObj.getNumeroAgencia(), reqObj.getNumeroConta());
        if (account == null) {
            throw new RuntimeException("Nao existe uma conta com essa com esse numero agencia e conta");
        }
        User user = account.getUser();
        PixKey pixKey = reqObj.toPixKey();

        List<PixKey> pixKeyList = account.getPixKeys();
        int pixKeyListSize = pixKeyList.size();

        validateCreatePixKey(pixKey, pixKeyList, account, user);

        if (user.isPessoaFisica() && pixKeyListSize >= 5) {
            throw new RuntimeException("Limite de 5 chaves por conta para Pessoa Física excedido");
        } else if (user.isPessoaJuridica() && pixKeyListSize >= 20) {
            throw new RuntimeException("Limite de 20 chaves por conta para Pessoa Jurídica excedido");
        }

        pixKey.setAtiva(true); // Por padrão, nova chave PIX é ativa
        pixKey.setAccount(account);

        return pixKeyRepository.save(pixKey);
    }

//    public List<PixKey> getAllPixKeys() {
//        return pixKeyRepository.findAll();
//    }
//
//    public ResponseEntity<?> getPixKeyById(UUID id) {
//        Optional<PixKey> pixKey = pixKeyRepository.findById(id);
//        if (pixKey.isPresent()) {
//            return ResponseEntity.ok(pixKey.get());
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chave Pix não encontrada");
//        }
//    }
//
//    public ResponseEntity<?> getPixKeysByType(String tipoChave) {
//        List<PixKey> pixKeys = pixKeyRepository.findByTipoChave(tipoChave);
//        if (!pixKeys.isEmpty()) {
//            return ResponseEntity.ok(pixKeys);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma chave Pix encontrada para o tipo especificado");
//        }
//    }
//
//    public ResponseEntity<?> getPixKeysByAgenciaConta(int numeroAgencia, int numeroConta) {
//        List<PixKey> pixKeys = pixKeyRepository.findByNumeroAgenciaAndNumeroConta(numeroAgencia, numeroConta);
//        if (!pixKeys.isEmpty()) {
//            return ResponseEntity.ok(pixKeys);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma chave Pix encontrada para a agência e conta especificadas");
//        }
//    }
//
//    public ResponseEntity<?> getPixKeysByCorrentista(String nomeCorrentista) {
//        // Verifica se o nome do correntista é fornecido
//        if (nomeCorrentista == null || nomeCorrentista.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O nome do correntista deve ser fornecido");
//        }
//
//        // Consulta no repositório pixKeyRepository
//        List<PixKey> pixKeys = pixKeyRepository.findByNomeCorrentista(nomeCorrentista);
//
//        // Verifica se foram encontradas chaves Pix para o correntista fornecido
//        if (!pixKeys.isEmpty()) {
//            return ResponseEntity.ok(pixKeys);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma chave Pix encontrada para o correntista especificado");
//        }
//    }
//
//    public ResponseEntity<?> getPixKeysByInclusao(LocalDate dataInclusao) {
//        // Verifica se foi fornecida a data de inclusão
//        if (dataInclusao == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inclusão deve ser fornecida para consulta");
//        }
//
//        // Consulta no repositório pixKeyRepository
//        List<PixKey> pixKeys = pixKeyRepository.findByCreatedAt(dataInclusao);
//
//        // Verifica se foram encontradas chaves Pix para a data de inclusão fornecida
//        if (!pixKeys.isEmpty()) {
//            return ResponseEntity.ok(pixKeys);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma chave Pix encontrada para a data de inclusão especificada");
//        }
//    }
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
//
//
//    @Transactional
//    public ResponseEntity<?> deletePixKey(UUID id) {
//        PixKey existingPixKey = pixKeyRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Chave Pix não encontrada"));
//
//        // Verifica se a chave já está inativa
//        if (!existingPixKey.isAtiva()) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
//                    .body("A chave Pix já está desativada");
//        }
//
//        // Inativa a chave Pix e registra a data e hora da inativação
//        existingPixKey.setAtiva(false);
//        existingPixKey.setInactivatedAt(LocalDateTime.now());
//
//        pixKeyRepository.save(existingPixKey);
//
//        return ResponseEntity.ok(existingPixKey);
//    }

    private void validateCreatePixKey(PixKey pixKey, List<PixKey> pixKeyList, Account account, User user) {
        if (pixKeyRepository.existsByValorChave(pixKey.getValorChave())) {
            throw new RuntimeException("Valor de chave já cadastrado");
        }

        // Validação adicional baseada no tipo de chave
        switch (pixKey.getTipoChave().toLowerCase()) {
            case "celular":
                validateCelular(pixKey.getValorChave());
                break;
            case "email":
                validateEmail(pixKey.getValorChave());
                break;
            case "cpf":
                if(user.isPessoaJuridica())
                    throw new IllegalArgumentException("Pessoa juridica nao pode cadastrar chave CPF");

                validateCPF(pixKey.getValorChave(), pixKeyList, account);
                break;
            case "cnpj":
                if(user.isPessoaFisica())
                    throw new IllegalArgumentException("Pessoa fisica nao pode cadastrar chave CNPJ");

                validateCNPJ(pixKey.getValorChave(), pixKeyList, account);
                break;
            case "aleatorio":
                validateChaveAleatoria(pixKey.getValorChave());
                break;
            default:
                throw new RuntimeException("Tipo de chave inválido");
        }
    }

//    private void validateUpdatedFields(PixKey pixKey) {
//        // Critério de aceite 6: Validar os valores alterados
//
//        // Validação do tipo da conta
//        if (!"corrente".equalsIgnoreCase(pixKey.getTipoConta()) && !"poupança".equalsIgnoreCase(pixKey.getTipoConta())) {
//            throw new RuntimeException("Tipo de conta inválido");
//        }
//
//        // Validação do número da agência
//        if (pixKey.getNumeroAgencia() == null || pixKey.getNumeroAgencia().toString().length() > 4) {
//            throw new RuntimeException("Número da agência inválido");
//        }
//
//        // Validação do número da conta
//        if (pixKey.getNumeroConta() == null || pixKey.getNumeroConta().toString().length() > 8) {
//            throw new RuntimeException("Número da conta inválido");
//        }
//
//        // Validação do nome do correntista
//        if (pixKey.getNomeCorrentista() == null || pixKey.getNomeCorrentista().length() > 30) {
//            throw new RuntimeException("Nome do correntista inválido");
//        }
//
//        // Validação do sobrenome do correntista
//        if (pixKey.getSobrenomeCorrentista() != null && pixKey.getSobrenomeCorrentista().length() > 45) {
//            throw new RuntimeException("Sobrenome do correntista inválido");
//        }
//    }

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
            throw new RuntimeException("E-mail inválido");
        }
    }

    private void validateCPF(String valorChave, List<PixKey> pixKeyList, Account account) {
        if(!account.getUser().getIdentificacao().equals(valorChave)) {
            throw new RuntimeException("A chave CPF deve ser igual o CPF da conta");
        }

        for (PixKey pixKey : pixKeyList) {
            if(pixKey.getTipoChave().equalsIgnoreCase("cpf")) {
                throw new RuntimeException("Chave CPF ja cadastrada para essa conta.");
            }
        }

        if (!isNumeric(valorChave)) {
            throw new RuntimeException("O CPF deve conter apenas números");
        }
        if (valorChave.length() != 11 || valorChave.matches("(\\d)\\1{10}")) {
            throw new RuntimeException("CPF inválido");
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
            throw new RuntimeException("CPF inválido");
        }
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        remainder = sum % 11;
        int digit2 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[10] != digit2) {
            throw new RuntimeException("CPF inválido");
        }
    }

    private void validateCNPJ(String valorChave, List<PixKey> pixKeyList, Account account) {
        if(!account.getUser().getIdentificacao().equals(valorChave)) {
            throw new RuntimeException("A chave CNPJ deve ser igual o CNPJ da conta");
        }

        for (PixKey pixKey : pixKeyList) {
            if(pixKey.getTipoChave().equalsIgnoreCase("cnpj")) {
                throw new RuntimeException("Chave CNPJ ja cadastrada para essa conta.");
            }
        }

        valorChave = valorChave.replaceAll("[^0-9]", "");
        if (valorChave.length() != 14 || valorChave.matches("(\\d)\\1{13}")) {
            throw new RuntimeException("CNPJ inválido");
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
            throw new RuntimeException("CNPJ inválido");
        }
        sum = 0;
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 13; i++) {
            sum += digits[i] * weights2[i];
        }
        remainder = sum % 11;
        int digit2 = (remainder < 2) ? 0 : (11 - remainder);
        if (digits[13] != digit2) {
            throw new RuntimeException("CNPJ inválido");
        }
    }

    private void validateChaveAleatoria(String valorChave) {
        if (!valorChave.matches("[a-zA-Z0-9]{36}")) {
            throw new RuntimeException("Chave aleatória inválida");
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