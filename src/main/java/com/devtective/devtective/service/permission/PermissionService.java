package com.devtective.devtective.service.permission;

import com.devtective.devtective.dominio.user.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("perm")
public class PermissionService {

    public boolean selfOrAdmin(Authentication auth, String username) {
        var principal = (AppUser) auth.getPrincipal();
        return principal.getUsername().equals(username) ||
                principal.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}