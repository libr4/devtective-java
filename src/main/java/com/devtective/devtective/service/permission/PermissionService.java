package com.devtective.devtective.service.permission;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.repository.ProjectLeaderRepository;
import com.devtective.devtective.repository.ProjectMemberRepository;
import com.devtective.devtective.repository.ProjectRepository;
import com.devtective.devtective.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("perm")
public class PermissionService {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    WorkerRepository workerRepository;
    @Autowired
    ProjectMemberRepository projectMemberRepository;
    @Autowired
    ProjectLeaderRepository projectLeaderRepository;

    public boolean selfOrAdmin(Authentication auth, String username) {
        var principal = (AppUser) auth.getPrincipal();
        return principal.getUsername().equals(username) ||
                principal.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean projectMemberOrAdmin(Authentication auth, UUID publicProjectId) {
        if (!isAuth(auth)) return false;
        if (isAdmin(auth)) return true;
        AppUser me = (AppUser) auth.getPrincipal();
        Worker worker = workerRepository.findByUserId(me);
        if (worker == null) return false;
        Long wid = worker.getId();
        Project p = projectRepository.findByPublicId(publicProjectId);
        if (p == null) return false;
        // owner
        if (p.getCreatedBy() != null && p.getCreatedBy().getId() == wid) return true;
        // member
        return projectMemberRepository.existsByProjectPublicIdAndWorkerId(publicProjectId, wid);
    }

        public boolean ownerOrLeadOrAdmin(Authentication auth, Long projectId) {
        if (!isAuth(auth)) return false;
        if (isAdmin(auth)) return true;
        AppUser me = (AppUser) auth.getPrincipal();
        Worker worker = workerRepository.findByUserId(me);
        if (worker == null) return false;
        Long wid = worker.getId();
        Project p = projectRepository.findById(projectId).orElse(null);
        if (p == null) return false;
        // owner
        if (p.getCreatedBy() != null && p.getCreatedBy().getId() == wid) return true;
        // lead
        return projectLeaderRepository.existsByProjectIdAndWorkerId(projectId, wid);
    }
    public boolean canReadOrCreateTask(Authentication auth, UUID projectPublicId) {
        if (!isAuth(auth)) return false;
        if (isAdmin(auth)) return true;

        Worker me = currentWorker(auth);
        if (me == null) return false;

        if (isProjectOwner(me.getId(), projectPublicId)) return true;
        if (isProjectLeader(me.getId(), projectPublicId)) return true;
        return isProjectMember(me.getId(), projectPublicId);
    }

    /** Only leads/owner/admin can update/delete tasks */
    public boolean canModifyTask(Authentication auth, UUID projectPublicId) {
        if (!isAuth(auth)) return false;
        if (isAdmin(auth)) return true;

        Worker me = currentWorker(auth);
        if (me == null) return false;

        if (isProjectOwner(me.getId(), projectPublicId)) return true;
        return isProjectLeader(me.getId(), projectPublicId);
    }
    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
    private boolean isAuth(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String);
    }
    private Worker currentWorker(Authentication auth) {
        AppUser principal = (AppUser) auth.getPrincipal();
        return workerRepository.findByUserId(principal);
    }

    private boolean isProjectOwner(Long workerId, UUID projectPublicId) {
        Project p = projectRepository.findByPublicId(projectPublicId);
        return p != null && p.getCreatedBy() != null && p.getCreatedBy().getId().equals(workerId);
    }

    private boolean isProjectLeader(Long workerId, UUID projectPublicId) {
        return projectLeaderRepository.existsByProjectPublicIdAndWorkerId(projectPublicId, workerId);
    }

    private boolean isProjectMember(Long workerId, UUID projectPublicId) {
        return projectMemberRepository.existsByProjectPublicIdAndWorkerId(projectPublicId, workerId);
    }

}