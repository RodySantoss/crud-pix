package com.cadastro.pix.domain.account.dto;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.dto.PixKeyDTO;
import com.cadastro.pix.interfaces.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountWithPixDTO implements BaseDTO {
    private UUID id;
    private String accountType;
    private Integer agencyNumber;
    private Integer accountNumber;
    private Boolean active;
    private LocalDateTime inactivatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PixKeyDTO> pixKeys;

    public AccountWithPixDTO(Account account) {
        this.id = account.getId();
        this.accountType = account.getAccountType();
        this.agencyNumber = account.getAgencyNumber();
        this.accountNumber = account.getAccountNumber();
        this.pixKeys = account.getPixKeys().stream()
                .map(PixKeyDTO::new)
                .collect(Collectors.toList());
        this.active = account.isActive();
        this.inactivatedAt = account.getInactivatedAt();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getUpdatedAt();
    }
}

