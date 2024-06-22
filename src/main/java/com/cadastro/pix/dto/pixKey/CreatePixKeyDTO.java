package com.cadastro.pix.dto.pixKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePixKeyDTO {
    private String keyType;
    private String keyValue;
    private Integer agencyNumber;
    private Integer accountNumber;
}
