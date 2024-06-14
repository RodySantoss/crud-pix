package com.cadastro.pix.domain.user.dto;

import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.interfaces.BaseDTO;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListDTO implements BaseDTO {
    private List<UserDTO> users;

    public static UserListDTO fromUsers(List<User> users) {
        List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return new UserListDTO(userDTOs);
    }
}