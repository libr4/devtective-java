package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.user.UserDiscoverability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDiscoverabilityRepository extends JpaRepository<UserDiscoverability, Long> {

    //Role findByRoleName(String roleName);
    Optional<UserDiscoverability> findById(Long id);

}
