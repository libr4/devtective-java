package com.devtective.devtective.service;

import org.springframework.stereotype.Service;

import com.devtective.devtective.dominio.user.Role;
import com.devtective.devtective.dominio.user.RoleConstants;
import com.devtective.devtective.dominio.user.UserDiscoverability;
import com.devtective.devtective.dominio.user.UserDiscoverabilityConstants;
import com.devtective.devtective.dominio.worker.Position;
import com.devtective.devtective.repository.PositionRepository;
import com.devtective.devtective.repository.RoleRepository;
import com.devtective.devtective.repository.UserDiscoverabilityRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultsCacheService {
    private UserDiscoverability defaultDiscoverability;
    private Role defaultRole;
    private Position defaultPosition;

    private final UserDiscoverabilityRepository userDiscoverabilityRepository;
    private final RoleRepository roleRepository;
    private final PositionRepository positionRepository;

    @PostConstruct
    public void loadDefaults() {
        this.defaultRole = roleRepository.findByRoleName(RoleConstants.USER.toString())
            .orElseThrow(() -> new IllegalStateException("Default ROLE not configured"));
        this.defaultDiscoverability = userDiscoverabilityRepository.findById(UserDiscoverabilityConstants.WORKSPACE)
            .orElseThrow(() -> new IllegalStateException("Default discoverability not configured"));
        this.defaultPosition = positionRepository.findByName(Position.DEFAULT_POSITION)
            .orElseThrow(() -> new IllegalStateException("Default POSITION not configured"));
    }

    public UserDiscoverability getDefaultDiscoverability() {
        return defaultDiscoverability;
    }
    public Role getDefaultRole() {
        return defaultRole;
    }
    public Position getDefaultPositon() {
        return defaultPosition;
    }
}
