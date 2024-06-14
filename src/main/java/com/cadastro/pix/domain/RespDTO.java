package com.cadastro.pix.domain;


import com.cadastro.pix.interfaces.BaseDTO;
import com.cadastro.pix.utils.RespDTOSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = RespDTOSerializer.class)
public class RespDTO {
    private HttpStatus httpStatus;
    private BaseDTO data;
    private String message;

    // Construtor para casos de sucesso
    public RespDTO(HttpStatus httpStatus, BaseDTO data) {
        this.httpStatus = httpStatus;
        this.data = data;
        this.message = null;
    }

    // Construtor para casos de erro
    public RespDTO(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.data = null;
        this.message = message;
    }
}