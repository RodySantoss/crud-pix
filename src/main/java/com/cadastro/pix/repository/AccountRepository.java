package com.cadastro.pix.repository;

import com.cadastro.pix.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
//import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findById(UUID id);
    Account findByNumeroAgenciaAndNumeroConta(Integer numeroAgencia, Integer numeroConta);
}