package com.cadastro.pix.domain.pixKey.dto;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.account.dto.SimpleAccountWithUserDTO;
import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.domain.user.dto.UserWithAccountsAndPixDTO;
import com.cadastro.pix.interfaces.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixKeyListWithAccountAndUserDTO implements BaseDTO {
    private List<PixKeyWithAccountDTO> pixKeys;

    public static PixKeyListWithAccountAndUserDTO fromPixKeys(List<PixKey> pixKeys) {
        List<PixKeyWithAccountDTO> pixKeyDTOs = pixKeys.stream()
                .map(PixKeyWithAccountDTO::new)
                .collect(Collectors.toList());
        return new PixKeyListWithAccountAndUserDTO(pixKeyDTOs);
    }

    public static PixKeyListWithAccountAndUserDTO fromPixKeys(List<PixKey> pixKeys, Account account) {
        List<PixKeyWithAccountDTO> pixKeyDTOs = pixKeys.stream()
                .map(pixKey -> new PixKeyWithAccountDTO(pixKey, account))
                .collect(Collectors.toList());
        return new PixKeyListWithAccountAndUserDTO(pixKeyDTOs);
    }
}