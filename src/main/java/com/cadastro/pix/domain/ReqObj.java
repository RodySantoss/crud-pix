package com.cadastro.pix.domain;

import com.cadastro.pix.domain.account.Account;
import com.cadastro.pix.domain.pixKey.PixKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqObj {
    private UUID pix_id;
    private String keyType;
    private String keyValue;
    private String accountType;
    private String personType;
    private Integer agencyNumber;
    private Integer accountNumber;
    private String userName;
    private String userLastName;
    private String identification;


    public PixKey toPixKey() {
       return new PixKey(this.pix_id, this.keyType, this.keyValue, null, null, null,
               null, null);
    }

    public Account toAccount() {
        List<PixKey> pixKeyList = new ArrayList<>();

        return new Account(null, this.accountType, this.agencyNumber, this.accountNumber, null, pixKeyList, null, null, null, null);
    }
}
