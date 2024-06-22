package com.cadastro.pix.dto.user;

import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.interfaces.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleUserDTO implements BaseDTO {
    private UUID id;
    private String personType;
    private String userName;
    private String userLastName;
    private String identification;
    private String phone;
    private String email;
    private Boolean active;

    public SimpleUserDTO(User user) {
        this.id = user.getId();
        this.personType = user.getPersonType();
        this.userName = user.getUserName();
        this.userLastName = user.getUserLastName();
        this.identification = user.getIdentification();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.active = user.isActive();
    }
}