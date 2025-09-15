package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.user.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

        @Query("""
          SELECT DISTINCT u
          FROM WorkspaceMember wm
            JOIN wm.worker w
            JOIN w.user u
          WHERE EXISTS (
            SELECT 1
            FROM WorkspaceMember wm8
              JOIN wm8.worker w8
              JOIN w8.user anchor
            WHERE wm8.workspace = wm.workspace
              AND anchor.publicId = :publicId
          )
          AND u.publicId <> :publicId
          AND (:discIds IS NULL OR u.discoverability.id = :discId)
        """)
        List<AppUser> findUsersSharingWorkspacesWithUserPublicId(
                @Param("publicId") UUID publicId,
                @Param("discId") Long discId);


        void deleteByUsername(String username);

        void deleteByUsernameStartingWith(String prefix);
}
