package com.cadastro.pix.domain.account.dto;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.dto.PixKeyDTO;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.domain.user.dto.SimpleUserDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.interfaces.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleAccountWithUserDTO implements BaseDTO {
    private UUID id;
    private String accountType;
    private Integer agencyNumber;
    private Integer accountNumber;
    private SimpleUserDTO user;
    private Boolean active;

    public SimpleAccountWithUserDTO(Account account) {
        User user = account.getUser();
        if(user  == null) {
            throw new EntityNotFoundException("Nenhum usuario foi encontrado");
        }

        this.id = account.getId();
        this.accountType = account.getAccountType();
        this.agencyNumber = account.getAgencyNumber();
        this.accountNumber = account.getAccountNumber();
        this.active = account.isActive();
        this.user = new SimpleUserDTO(user);
    }

    public SimpleAccountWithUserDTO(Account account, User user) {
        this.id = account.getId();
        this.accountType = account.getAccountType();
        this.agencyNumber = account.getAgencyNumber();
        this.accountNumber = account.getAccountNumber();
        this.active = account.isActive();
        this.user = new SimpleUserDTO(user);
    }

    public SimpleAccountWithUserDTO(UUID id) {
        this.id = id;
    }
}

