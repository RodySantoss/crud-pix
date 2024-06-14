package com.cadastro.pix.domain.user;

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
import java.util.*;

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
    @Column(name = "person_type", nullable = false, length = 10)
    private String personType;

    @NotNull
    @Size(max = 30, message = "Nome do correntista muito longo")
    @Column(name = "user_name", nullable = false, length = 30)
    private String userName;

    @Size(max = 45, message = "Sobrenome do correntista muito longo")
    @Column(name = "user_last_name", length = 45)
    private String userLastName;

    @Size(max = 14, message = "Numero da indentificaçao muito longo")
    @Column(name = "identification", nullable = false, length = 14)
    private String identification;

    @Size(max = 15, message = "Numero de celular muito longo")
    @Column(name = "phone", nullable = false, length = 14)
    private String phone;

    @Size(max = 77, message = "Email muito longo")
    @Column(name = "email", nullable = false, length = 77)
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

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

    @JsonIgnore
    public boolean isPhysicalPerson() {
        return "fisica".equalsIgnoreCase(this.personType);
    }

    @JsonIgnore
    public boolean isLegalPerson() {
        return "juridica".equalsIgnoreCase(this.personType);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", personType='" + personType + '\'' +
                ", userName='" + userName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", identification='" + identification + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", inactivatedAt=" + inactivatedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
