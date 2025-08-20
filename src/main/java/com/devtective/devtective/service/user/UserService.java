package com.devtective.devtective.service.user;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.user.*;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.exception.ConflictException;
import com.devtective.devtective.exception.NotFoundException;
import com.devtective.devtective.repository.ProjectRepository;
import com.devtective.devtective.repository.RoleRepository;
import com.devtective.devtective.repository.UserRepository;
import com.devtective.devtective.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private UserRepository repository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    public UserResponseDTO createUser(UserRequestDTO data) {

        AppUser userExists = repository.findByUsername(data.username());
        if (userExists != null) {
            throw new ConflictException("Username already exists: " + data.username());
        }

        AppUser user = new AppUser();

        user.setUsername(data.username());
        user.setEmail(data.email());

        String hashedPassword = passwordEncoder.encode(data.password());

        //Role newRole = roleRepository.findById(data.roleId()).orElseThrow(() -> new NotFoundException("Role with ID: " + data.roleId() + " not found"));
        //user.setRole(newRole);

        Role defaultRole = roleRepository.findByRoleName("USER");

        if (defaultRole == null) {
            throw new NotFoundException("Default role not configured");
        }
        user.setRole(defaultRole);

        user.setPasswordHash(hashedPassword);

        AppUser newUser = repository.save(user);

        if (newUser.getRole() == null) {
            throw new ConflictException("User doesn't have a role: " + data.username());
        }
        UserResponseDTO response = new UserResponseDTO(newUser.getUsername(), newUser.getEmail(), newUser.getRole().getId());

        return response;
    }

    public List<AppUser> getAllUsers() {
        List<AppUser> users = repository.findAll();
        return users;
    }
    public Page<UserResponseDTO> getAllUsersPaginated(Pageable pageable) {
        Page<UserResponseDTO> users = repository.findAll(pageable)
                .map(u -> new UserResponseDTO(u.getUsername(), u.getEmail(), u.getRole().getId()));
        return users;
    }

    public AppUser findByUsername(String username) {
        AppUser user = repository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found: " + username);
        }
        return user;
    }

    @PreAuthorize("@perm.selfOrAdmin(authentication, #username)")
    public UserResponseDTO fetchOwnUser(String username) {
        AppUser user = repository.findByUsername(username);
        UserResponseDTO response = new UserResponseDTO(user.getUsername(), user.getEmail(), user.getRole().getId());
        return response;
    }

    public AppUser updateUser(UserRequestDTO data) {

        AppUser user = findByUsername(data.username());

        if (user == null) {
            throw new NotFoundException("User not found: " + data.username());
        }

        user.setUsername(data.username());
        user.setEmail(data.email());

        Role newRole = new Role(data.roleId());
        user.setRole(newRole);
        if (data.password() != null && !data.password().isBlank()) {
            String hashedPassword = passwordEncoder.encode(data.password());
            user.setPasswordHash(hashedPassword);
        }

        return repository.save(user);
    }

    public UserResponseDTO updateUserResponse(String username, UserRequestDTO data) {

        AppUser user = findByUsername(username);

        if (user == null) {
            throw new NotFoundException("User not found: " + data.username());
        }

        user.setUsername(data.username());
        user.setEmail(data.email());

        Role newRole = new Role(data.roleId());
        user.setRole(newRole);
        if (data.password() != null && !data.password().isBlank()) {
            String hashedPassword = passwordEncoder.encode(data.password());
            user.setPasswordHash(hashedPassword);
        }

        AppUser newUser = repository.save(user);
        UserResponseDTO response = new UserResponseDTO(newUser.getUsername(), newUser.getEmail(), newUser.getRole().getId());
        return response;
    }

    @PreAuthorize("@perm.selfOrAdmin(authentication, #username)")
    public UserResponseDTO updateOwnUser(UserRequestDTO dto, String username) {
        AppUser newUser = updateUser(dto);
        UserResponseDTO response = new UserResponseDTO(newUser.getUsername(), newUser.getEmail(), newUser.getRole().getId());
        return response;
    }

    public void deleteByUsername(String username) {
        AppUser user = findByUsername(username);
        System.out.println("ABOUT TO DELETE: " + username);

        if (user == null) {
            throw new NotFoundException("User not found: " + username);
        }

        Worker worker = workerRepository.findByUserId(user);

        if (worker != null) {
            List<Project> workerProjects = projectRepository.findByCreatedBy(worker);
            if (!workerProjects.isEmpty()) {
                projectRepository.deleteAll(workerProjects);
            }
            workerRepository.delete(worker);
        }

        repository.delete(user);
    }

    // TO DO
    public UserResponseDTO updateOwnProfile(Long ownUserId, UserRequestDTO dto) {
        return null;
    }

}