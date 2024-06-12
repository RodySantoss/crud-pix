package com.cadastro.pix.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pix_key")
public class PixKey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "tipo_chave", nullable = false, length = 9)
    private String tipoChave;

    @NotNull
    @Column(name = "valor_chave", nullable = false, length = 77, unique = true)
    private String valorChave;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;  // Adicionando referência ao Account

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
