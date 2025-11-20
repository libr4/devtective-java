package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.user.Role;
import com.devtective.devtective.dominio.worker.Position;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByName(String name);

}
