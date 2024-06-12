package com.cadastro.pix.repository;

import com.cadastro.pix.domain.Account;
import com.cadastro.pix.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
//import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findById(UUID id);
    User findByIdentificacao(String identificacao);
}