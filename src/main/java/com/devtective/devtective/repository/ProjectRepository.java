package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<AppUser, Long> {
}
