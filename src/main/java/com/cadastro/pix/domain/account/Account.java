package com.cadastro.pix.domain.account;

import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.account.CreateAccountDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "accountType must not be null")
    @Pattern(regexp = "^(corrente|poupança)$", message = "Invalid account type")
    @Column(name = "account_type", nullable = false, length = 10)
    private String accountType;

    @NotNull(message = "agencyNumber must not be null")
    @Min(value = 1, message = "Invalid agency number")
    @Max(value = 9999, message = "Invalid agency number")
    @Column(name = "agency_number", nullable = false)
    private Integer agencyNumber;

    @NotNull(message = "accountNumber must not be null")
    @Min(value = 1, message = "Invalid account number")
    @Max(value = 99999999, message = "Invalid account number")
    @Column(name = "account_number", nullable = false)
    private Integer accountNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Adicionando referência ao User

    @JsonIgnore
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<PixKey> pixKeys = new ArrayList<>();

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

    public Account(CreateAccountDTO accountDTO) {
        this.accountType = accountDTO.getAccountType();
        this.agencyNumber = accountDTO.getAgencyNumber();
        this.accountNumber = accountDTO.getAccountNumber();
    }

    // Custom methods
    public boolean isActive() {
        return this.active;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountType='" + accountType + '\'' +
                ", agencyNumber=" + agencyNumber +
                ", accountNumber=" + accountNumber +
                ", active=" + active +
                ", inactivatedAt=" + inactivatedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}