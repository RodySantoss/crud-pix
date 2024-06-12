package com.cadastro.pix.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqObj {
    private UUID pix_id;
    private String tipoChave;
    private String valorChave;
    private String tipoConta;
    private String tipoPessoa;
    private Integer numeroAgencia;
    private Integer numeroConta;
    private String nomeCorrentista;
    private String sobrenomeCorrentista;
    private String identificacao;


    public PixKey toPixKey() {
       return new PixKey(this.pix_id, this.tipoChave, this.valorChave, null, null, null,
               null, null);
    }

    public Account toAccount() {
        List<PixKey> pixKeyList = new ArrayList<>();

        return new Account(null, this.tipoConta, this.numeroAgencia, this.numeroConta, null, pixKeyList, null, null, null, null);
    }
}
