package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.task.TaskType;
import com.devtective.devtective.dominio.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
