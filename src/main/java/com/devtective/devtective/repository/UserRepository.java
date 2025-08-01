package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
        AppUser findByUsername(String username);

        AppUser findByUserId(Long userId);

        void deleteByUsername(String username);
}
