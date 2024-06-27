package com.cadastro.pix.repository;

import com.cadastro.pix.domain.pixKey.PixKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PixKeyRepository extends JpaRepository<PixKey, UUID> {
    boolean existsByKeyValueAndActive(String keyValue, boolean active);

    @Query("SELECT p FROM PixKey p WHERE p.account.user.userName = :userName")
    List<PixKey> findByUserName(@Param("userName") String userName);

    @Query("SELECT p FROM PixKey p WHERE p.createdAt >= :startOfDay AND p.createdAt < :endOfDay")
    List<PixKey> findByCreatedAtBetween(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT p FROM PixKey p WHERE p.inactivatedAt >= :startOfDay AND p.createdAt < :endOfDay")
    List<PixKey> findByInactivatedAtBetween(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    List<PixKey> findByKeyType(String keyType);

}