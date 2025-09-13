package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.user.Role;
import com.devtective.devtective.dominio.worker.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Position findByName(String name);

}
