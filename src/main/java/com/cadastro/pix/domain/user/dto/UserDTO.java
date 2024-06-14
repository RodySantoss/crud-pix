package com.cadastro.pix.domain.user.dto;

import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.interfaces.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO implements BaseDTO {
    private UUID id;
    private String personType;
    private String userName;
    private String userLastName;
    private String identification;
    private String phone;
    private String email;
    private Boolean active;
    private LocalDateTime inactivatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserDTO(User user) {
        this.id = user.getId();
        this.personType = user.getPersonType();
        this.userName = user.getUserName();
        this.userLastName = user.getUserLastName();
        this.identification = user.getIdentification();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.active = user.isActive();
        this.inactivatedAt = user.getInactivatedAt();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    public UserDTO(UUID id) {
        this.id = id;
    }
}