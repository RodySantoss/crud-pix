package com.cadastro.pix.domain.user.dto;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.account.dto.SimpleAccountWithPixDTO;
import com.cadastro.pix.domain.user.User;
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
public class UserWithAccountsAndPixDTO implements BaseDTO {
    private UUID id;
    private String personType;
    private String userName;
    private String userLastName;
    private String identification;
    private String phone;
    private String email;
    private Boolean active;
    private List<SimpleAccountWithPixDTO> accounts;
    private SimpleAccountWithPixDTO account;

    public UserWithAccountsAndPixDTO(User user) {
        List<Account> accounts = user.getAccounts();

        this.id = user.getId();
        this.personType = user.getPersonType();
        this.userName = user.getUserName();
        this.userLastName = user.getUserLastName();
        this.identification = user.getIdentification();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.active = user.isActive();
        this.accounts = accounts.stream()
                .map(SimpleAccountWithPixDTO::new)
                .collect(Collectors.toList());
    }


    public UserWithAccountsAndPixDTO(Account account) {
        User user = account.getUser();

        this.id = user.getId();
        this.personType = user.getPersonType();
        this.userName = user.getUserName();
        this.userLastName = user.getUserLastName();
        this.identification = user.getIdentification();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.active = user.isActive();
        this.account = new SimpleAccountWithPixDTO(account);
    }
}

