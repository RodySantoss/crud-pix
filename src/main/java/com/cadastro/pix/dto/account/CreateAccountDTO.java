package com.cadastro.pix.dto.account;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountDTO {
    @NotNull(message = "accountType must not be null")
    @Pattern(regexp = "^(corrente|poupança)$", message = "Invalid account type")
    private String accountType;

    @NotNull(message = "agencyNumber must not be null")
    @Min(value = 1, message = "Invalid agency number")
    @Max(value = 9999, message = "Invalid agency number")
    private Integer agencyNumber;

    @NotNull(message = "accountNumber must not be null")
    @Min(value = 1, message = "Invalid account number")
    @Max(value = 99999999, message = "Invalid account number")
    private Integer accountNumber;

    @NotNull(message = "identification must not be null")
    @Size(max = 14, message = "Identification number too long")
    private String identification;
}
