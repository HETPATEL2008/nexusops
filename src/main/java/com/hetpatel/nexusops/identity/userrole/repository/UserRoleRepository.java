package com.hetpatel.nexusops.identity.userrole.repository;

import com.hetpatel.nexusops.identity.userrole.entity.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    boolean existsByUserIdAndRoleId(Long userId, Long roleId);

    List<UserRole> findAllByUserId(Long userId);

    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}
