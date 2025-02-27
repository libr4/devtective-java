package com.devtective.devtective.service.user;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.Role;
import com.devtective.devtective.dominio.user.RoleConstants;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    public AppUser createUser(UserRequestDTO data) {
        AppUser user = new AppUser();

        user.setUsername(data.username());
        user.setEmail(data.email());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(data.password());

        Role newRole = new Role(RoleConstants.USER);
        user.setRole(newRole);

        user.setPasswordHash(hashedPassword);

        repository.save(user);

        return user;
    }

    public List<AppUser> getAllUsers() {
        List<AppUser> users = repository.findAll();
        return users;
    }
}
