package com.cadastro.pix.repository;

import com.cadastro.pix.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findById(UUID id);
    Account findByAgencyNumberAndAccountNumber(Integer numeroAgencia, Integer numeroConta);
}