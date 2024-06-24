package com.cadastro.pix.dto.pixKey;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePixKeyDTO {

    @NotNull
    @Column(name = "key_type", nullable = false, length = 9)
    private String keyType;

    @NotNull
    @Column(name = "key_value", nullable = false, length = 77)
    private String keyValue;

    @NotNull
    @Min(value = 1, message = "Invalid agency number")
    @Max(value = 9999, message = "Invalid agency number")
    private Integer agencyNumber;

    @NotNull
    @Min(value = 1, message = "Invalid account number")
    @Max(value = 99999999, message = "Invalid account number")
    private Integer accountNumber;
}
