package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserWithFullNameDTO;
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
        Optional<AppUser> findByUsername(String username);

        AppUser findByEmail(String email);
        AppUser findByUserId(Long userId);

        Page<AppUser> findAll(Pageable pageable);

        Optional<AppUser> findByPublicId(UUID publicId);
        List<AppUser> findByPublicIdIn(Collection<UUID> ids);

        @Query("""
          select distinct
                 u.publicId as publicId, u.username  as username,
                 coalesce(nullif(
                     trim(concat(coalesce(w.firstName, ''), ' ', coalesce(w.lastName, ''))),
                     ''
                   ),
                   u.username
                 ) as displayName
          from WorkspaceMember wm
            join wm.worker w
            join w.userId u
          where exists (
            select 1
            from WorkspaceMember wm8
              join wm8.worker w8
              join w8.userId anchor
            where wm8.workspace = wm.workspace
              and anchor.publicId = :publicId
          )
          and u.publicId <> :publicId
            and u.discoverability.id <> :#{T(com.devtective.devtective.dominio.user.UserDiscoverabilityConstants).NONE}
          and u.discoverability.id = :discId
        """)
        List<UserWithFullNameDTO> findUsersSharingWorkspace(
                @Param("publicId") UUID publicId,
                @Param("discId") Long discId);

        void deleteByUsername(String username);

        void deleteByUsernameStartingWith(String prefix);
}
