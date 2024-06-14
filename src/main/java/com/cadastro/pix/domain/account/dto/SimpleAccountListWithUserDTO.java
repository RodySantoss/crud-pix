package com.cadastro.pix.domain.account.dto;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.domain.user.dto.SimpleUserDTO;
import com.cadastro.pix.domain.user.dto.UserListWithAccountAndPixDTO;
import com.cadastro.pix.domain.user.dto.UserWithAccountsAndPixDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
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
public class SimpleAccountListWithUserDTO implements BaseDTO {
    private List<SimpleAccountWithUserDTO> accounts;

    public static SimpleAccountListWithUserDTO fromAccounts(List<Account> accounts) {
        List<SimpleAccountWithUserDTO> accountDTOs = accounts.stream()
                .map(SimpleAccountWithUserDTO::new)
                .collect(Collectors.toList());
        return new SimpleAccountListWithUserDTO(accountDTOs);
    }
}

