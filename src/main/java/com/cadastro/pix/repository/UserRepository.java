package com.cadastro.pix.repository;

import com.cadastro.pix.domain.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findById(UUID id);

    @EntityGraph(attributePaths = {"accounts", "accounts.pixKeys"})
    User findWithAccountsAndPixKeysById(UUID id);

    User findByIdentification(String identification);

    List<User> findByUserName(String userName);
}