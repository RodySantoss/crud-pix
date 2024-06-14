package com.cadastro.pix.domain.pixKey.dto;

import com.cadastro.pix.domain.pixKey.PixKey;
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
public class PixKeyDTO implements BaseDTO {
    private UUID id;
    private String keyType;
    private String keyValue;
    private Boolean active;
    private LocalDateTime inactivatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PixKeyDTO(PixKey pixKey) {
        this.id = pixKey.getId();
        this.keyType = pixKey.getKeyType();
        this.keyValue = pixKey.getKeyValue();
        this.active = pixKey.isActive();
        this.inactivatedAt = pixKey.getInactivatedAt();
        this.createdAt = pixKey.getCreatedAt();
        this.updatedAt = pixKey.getUpdatedAt();
    }

    public PixKeyDTO(UUID id) {
        this.id = id;
    }
}