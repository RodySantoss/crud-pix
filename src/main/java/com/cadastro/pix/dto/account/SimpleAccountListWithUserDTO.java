package com.cadastro.pix.dto.account;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.interfaces.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
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

