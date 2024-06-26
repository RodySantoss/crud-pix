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

    @NotNull(message = "Person type must not be null")
    @Pattern(regexp = "^(fisica|juridica)$", message = "Invalid person type")
    @Column(name = "person_type", nullable = false, length = 10)
    private String personType;

    @NotNull(message = "User name must not be null")
    @Size(max = 30, message = "User name too long")
    @Column(name = "user_name", nullable = false, length = 30)
    private String userName;

    @Size(max = 45, message = "User last name too long")
    @Column(name = "user_last_name", length = 45)
    private String userLastName;

    @NotNull(message = "Identification must not be null")
    @Size(max = 14, message = "Identification number too long")
    @Column(name = "identification", nullable = false, length = 14)
    private String identification;

    @NotNull(message = "Phone must not be null")
    @Size(max = 15, message = "Phone number too long")
    @Column(name = "phone", nullable = false, length = 14)
    private String phone;

    @NotNull(message = "Email must not be null")
    @Size(max = 77, message = "Email too long")
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
    public boolean isIndividualPerson() {
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
