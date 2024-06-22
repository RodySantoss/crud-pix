package com.cadastro.pix.dto.pixKey;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.dto.account.SimpleAccountWithUserDTO;
import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.interfaces.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixKeyWithAccountDTO implements BaseDTO {
    private UUID id;
    private String keyType;
    private String keyValue;
    private SimpleAccountWithUserDTO account;
    private Boolean active;
    private LocalDateTime inactivatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PixKeyWithAccountDTO(PixKey pixKey) {
        Account account = pixKey.getAccount();
        if (account == null) {
            throw new EntityNotFoundException("Account not found");
        }

        this.id = pixKey.getId();
        this.keyType = pixKey.getKeyType();
        this.keyValue = pixKey.getKeyValue();
        this.active = pixKey.isActive();
        this.inactivatedAt = pixKey.getInactivatedAt();
        this.createdAt = pixKey.getCreatedAt();
        this.updatedAt = pixKey.getUpdatedAt();
        this.account = new SimpleAccountWithUserDTO(account);
    }

    public PixKeyWithAccountDTO(PixKey pixKey, Account account) {
        this.id = pixKey.getId();
        this.keyType = pixKey.getKeyType();
        this.keyValue = pixKey.getKeyValue();
        this.active = pixKey.isActive();
        this.inactivatedAt = pixKey.getInactivatedAt();
        this.createdAt = pixKey.getCreatedAt();
        this.updatedAt = pixKey.getUpdatedAt();
        this.account = new SimpleAccountWithUserDTO(account);
    }
}