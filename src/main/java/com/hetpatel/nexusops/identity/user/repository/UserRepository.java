package com.hetpatel.nexusops.identity.user.repository;

import com.hetpatel.nexusops.identity.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query(value = """
            UPDATE users
            SET deleted = false,
                deleted_at = NULL
            WHERE id = :id
              AND deleted = true
            """, nativeQuery = true)
    int restoreById(@Param("id") Long id);
}
