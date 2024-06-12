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
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Pattern(regexp = "^(corrente|poupança)$", message = "Tipo de conta inválido")
    @Column(name = "tipo_conta", nullable = false, length = 10)
    private String tipoConta;

    @NotNull
    @Min(value = 1, message = "Número da agência inválido")
    @Max(value = 9999, message = "Número da agência inválido")
    @Column(name = "numero_agencia", nullable = false)
    private Integer numeroAgencia;

    @NotNull
    @Min(value = 1, message = "Número da conta inválido")
    @Max(value = 99999999, message = "Número da conta inválido")
    @Column(name = "numero_conta", nullable = false)
    private Integer numeroConta;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Adicionando referência ao User

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<PixKey> pixKeys = new ArrayList<>();

    @Column(name = "ativa", nullable = false)
    private Boolean ativa;

    @Column(name = "inactivated_at")
    private LocalDateTime inactivatedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //deixa o nome da função mais fácil de entender
    public boolean isAtiva() {
        return this.getAtiva();
    }
}
