package com.cadastro.pix.domain.pixKey.dto;

import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.interfaces.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplePixKeyDTO implements BaseDTO {
    private UUID id;
    private String keyType;
    private String keyValue;
    private Boolean active;

    public SimplePixKeyDTO(PixKey pixKey) {
        this.id = pixKey.getId();
        this.keyType = pixKey.getKeyType();
        this.keyValue = pixKey.getKeyValue();
        this.active = pixKey.isActive();
    }
}