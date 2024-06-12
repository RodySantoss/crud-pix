package com.cadastro.pix.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Pattern(regexp = "^(fisica|juridica)$", message = "Tipo de pessoa inválido")
    @Column(name = "tipo_pessoa", nullable = false, length = 10)
    private String tipoPessoa;

    @NotNull
    @Size(max = 30, message = "Nome do correntista muito longo")
    @Column(name = "nome_correntista", nullable = false, length = 30)
    private String nomeCorrentista;

    @Size(max = 45, message = "Sobrenome do correntista muito longo")
    @Column(name = "sobrenome_correntista", length = 45)
    private String sobrenomeCorrentista;

    @Size(max = 14, message = "CPF ou CNPJ muito longo")
    @Column(name = "identificaçao", nullable = false, length = 14)
    private String identificacao;

    @Size(max = 15, message = "Numero de celular muito longo")
    @Column(name = "celular", nullable = false, length = 14)
    private String celular;

    @Size(max = 77, message = "CPF ou CNPJ muito longo")
    @Column(name = "email", nullable = false, length = 77)
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "data_inativo")
    private LocalDateTime inativoEm;

    @CreationTimestamp
    @Column(name = "data_criado", updatable = false)
    private LocalDateTime criado;

    @UpdateTimestamp
    @Column(name = "data_atualizado")
    private LocalDateTime atualizado;

    //deixa o nome da função mais fácil de entender
    public boolean isAtivo() {
        return this.getAtivo();
    }

    @JsonIgnore
    public boolean isPessoaFisica() {
        return this.tipoPessoa.equalsIgnoreCase("fisica");
    }

    @JsonIgnore
    public boolean isPessoaJuridica() {
        return this.tipoPessoa.equalsIgnoreCase("juridica");
    }

}
