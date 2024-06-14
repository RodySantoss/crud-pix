package com.cadastro.pix.domain.pixKey;

import com.cadastro.pix.domain.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "key_type", nullable = false, length = 9)
    private String keyType;

    @NotNull
    @Column(name = "key_value", nullable = false, length = 77, unique = true)
    private String keyValue;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;  // Adicionando referência ao Account

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "inactivated_at")
    private LocalDateTime inactivatedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Custom methods
    public boolean isActive() {
        return this.active;
    }

    @Override
    public String toString() {
        return "PixKey{" +
                "id=" + id +
                ", keyType='" + keyType + '\'' +
                ", keyValue='" + keyValue + '\'' +
                ", active=" + active +
                ", inactivatedAt=" + inactivatedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
