package com.cadastro.pix.domain.account.dto;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.dto.PixKeyDTO;
import com.cadastro.pix.interfaces.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleAccountWithPixDTO implements BaseDTO {
    private UUID id;
    private String accountType;
    private Integer agencyNumber;
    private Integer accountNumber;
    private List<PixKeyDTO> pixKeys;
    private Boolean active;

    public SimpleAccountWithPixDTO(Account account) {
        this.id = account.getId();
        this.accountType = account.getAccountType();
        this.agencyNumber = account.getAgencyNumber();
        this.accountNumber = account.getAccountNumber();
        this.active = account.isActive();
        this.pixKeys = account.getPixKeys().stream()
                .map(PixKeyDTO::new)
                .collect(Collectors.toList());
    }
}

