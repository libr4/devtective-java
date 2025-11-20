package com.devtective.devtective.service.user;

import com.devtective.devtective.common.Sanitizer;
import com.devtective.devtective.common.dto.Name;
import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.user.*;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.exception.NotFoundException;
import com.devtective.devtective.repository.*;
import com.devtective.devtective.service.DefaultsCacheService;
import com.devtective.devtective.service.user.validation.UserValidationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private PositionRepository positionRepository;
    @Autowired
    private UserDiscoverabilityRepository userDiscoverabilityRepository;
    @Autowired
    private UserValidationService userValidationService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DefaultsCacheService defaultsCacheService;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser createUser(UserRequestDTO data) {
        userValidationService.validateCreateUser(data);

        AppUser user = AppUser.create(data.username(), 
            data.email(), 
            passwordEncoder.encode(data.password()),
            defaultsCacheService.getDefaultDiscoverability(), 
            defaultsCacheService.getDefaultRole());

        return repository.save(user);
    }

    @Transactional
    public UserResponseDTO register(UserRequestDTO data) {
        AppUser newUser = createUser(data);
        Name fullName = Name.parseFullName(data.fullName());
        Worker newWorker = Worker.create(
            fullName.firstName(), 
            fullName.lastName(), 
            newUser, 
            defaultsCacheService.getDefaultPositon());
        workerRepository.save(newWorker);
        return new UserResponseDTO(newUser.getUsername(), newUser.getEmail(), newUser.getRole().getId());
    }

    public Page<UserResponseDTO> getAllUsersPaginated(Pageable pageable) {
        Page<UserResponseDTO> users = repository.findAll(pageable)
                .map(u -> new UserResponseDTO(u.getUsername(), u.getEmail(), u.getRole().getId()));
        return users;
    }

    public AppUser findByUsername(String username) {
        AppUser user = repository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found: " + username));
        return user;
    }

    @PreAuthorize("@perm.selfOrAdmin(authentication, #username)")
    public UserResponseDTO fetchOwnUser(String username) {
        AppUser user = repository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found: " + username));
        return new UserResponseDTO(user.getUsername(), user.getEmail(), user.getRole().getId());
    }

    public AppUser updateUser(UserRequestDTO data) {
        AppUser user = findByUsername(data.username());
        user.setUsername(data.username());
        user.setEmail(data.email());

        if (data.password() != null && !data.password().isBlank()) {
            String hashedPassword = passwordEncoder.encode(data.password());
            user.setPasswordHash(hashedPassword);
        }

        return repository.save(user);
    }

    public UserResponseDTO updateUserResponse(String username, UserRequestDTO data) {
        AppUser newUser = updateUser(data);
        return new UserResponseDTO(newUser.getUsername(), 
            newUser.getEmail(), 
            newUser.getRole().getId());
    }

    @PreAuthorize("@perm.selfOrAdmin(authentication, #username)")
    public UserResponseDTO updateOwnUser(UserRequestDTO dto, String username) {
        AppUser newUser = updateUser(dto);
        UserResponseDTO response = new UserResponseDTO(
            newUser.getUsername(), 
            newUser.getEmail(), 
            newUser.getRole().getId());
        return response;
    }

    public CurrentUserResponseDTO getCurrentUserResponse(AppUser me) {
        String displayName = getFormattedWorkerDisplayNameByUser(me);
        CurrentUserResponseDTO cUserRes = new CurrentUserResponseDTO(
                me.getUsername(), 
                me.getEmail(),
                me.getRole().getId(), 
                me.getPublicId(), 
                displayName);
        return cUserRes;
    }

    public String getFormattedWorkerDisplayNameByUser(AppUser user) {
        Worker w = workerRepository.findByUserId(user).orElseThrow(() -> new NotFoundException("Worker not found!"));
        String firstName = Sanitizer.sanitizeString(w.getFirstName());
        String lastName = Sanitizer.sanitizeString(w.getLastName());
       return (new Name(firstName, lastName)).getDisplayName();
    }

    @Transactional
    public void deleteByUsername(String username) {
        AppUser user = repository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found: " + username));
        userValidationService.validateCommonUser(user);
        Worker worker = workerRepository.findByUserId(user)
            .orElseThrow(() -> new NotFoundException("Worker not found!"));

        List<Project> workerProjects = projectRepository.findByCreatedBy(worker);
        if (!workerProjects.isEmpty()) {
            projectRepository.deleteAll(workerProjects);
        }
        workerRepository.delete(worker);
        repository.delete(user);
    }

    // TO DO
    public UserResponseDTO updateOwnProfile(Long ownUserId, UserRequestDTO dto) {
        return null;
    }

    public List<UserWithFullNameDTO> getRelatedUsers(AppUser me) {
        return repository.
            findUsersSharingWorkspace(me.getPublicId(), 
                me.getDiscoverability().getId());
    }
}