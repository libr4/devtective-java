package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.user.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<AppUser, Long> {
        @EntityGraph(attributePaths = "role")
        AppUser findByUsername(String username);

        AppUser findByEmail(String email);
        AppUser findByUserId(Long userId);

        Page<AppUser> findAll(Pageable pageable);

        Optional<AppUser> findByPublicId(UUID publicId);
        List<AppUser> findByPublicIdIn(Collection<UUID> ids);

        void deleteByUsername(String username);

        void deleteByUsernameStartingWith(String prefix);
}
