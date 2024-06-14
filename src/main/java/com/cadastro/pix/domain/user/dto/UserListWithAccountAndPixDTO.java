package com.cadastro.pix.domain.user.dto;

import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.interfaces.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListWithAccountAndPixDTO implements BaseDTO {
    private List<UserWithAccountsAndPixDTO> users;

    public static UserListWithAccountAndPixDTO fromUsers(List<User> users) {
        List<UserWithAccountsAndPixDTO> userDTOs = users.stream()
                .map(UserWithAccountsAndPixDTO::new)
                .collect(Collectors.toList());
        return new UserListWithAccountAndPixDTO(userDTOs);
    }
}