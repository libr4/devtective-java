package com.devtective.devtective.service.worker;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.Role;
import com.devtective.devtective.dominio.user.RoleConstants;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.repository.UserRepository;
import com.devtective.devtective.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    public AppUser createUser(UserRequestDTO data) {
        AppUser user = new AppUser();

        user.setUsername(data.username());
        user.setEmail(data.email());

        String hashedPassword = passwordEncoder.encode(data.password());

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

    public AppUser findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public AppUser updateUser(UserRequestDTO data) {

        AppUser user = findByUsername(data.username());

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + data.username());
        }

        user.setUsername(data.username());
        user.setEmail(data.email());

        String hashedPassword = passwordEncoder.encode(data.password());

        Role newRole = new Role(data.roleId());
        user.setRole(newRole);

        user.setPasswordHash(hashedPassword);

        return repository.save(user);
    }

}
