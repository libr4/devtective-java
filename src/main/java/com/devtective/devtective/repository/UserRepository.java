package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.user.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
        @EntityGraph(attributePaths = "role")
        AppUser findByUsername(String username);

        AppUser findByUserId(Long userId);

        Page<AppUser> findAll(Pageable pageable);

        void deleteByUsername(String username);
}
