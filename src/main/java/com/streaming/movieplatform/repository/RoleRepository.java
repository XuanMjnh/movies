package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.Role;
import com.streaming.movieplatform.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
