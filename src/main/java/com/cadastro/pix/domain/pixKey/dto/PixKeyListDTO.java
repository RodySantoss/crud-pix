package com.cadastro.pix.domain.pixKey.dto;

import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.interfaces.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixKeyListDTO implements BaseDTO {
    private List<PixKeyDTO> pixKeys;

    public static PixKeyListDTO fromPixKeys(List<PixKey> pixKeys) {
        List<PixKeyDTO> pixKeysDTOs = pixKeys.stream()
                .map(PixKeyDTO::new)
                .collect(Collectors.toList());
        return new PixKeyListDTO(pixKeysDTOs);
    }
}