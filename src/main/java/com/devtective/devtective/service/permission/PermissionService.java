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

    public boolean projectMemberOrAdmin(Authentication auth, Long projectId) {
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
            // member
            return projectMemberRepository.existsByProject_IdAndWorker_Id(projectId, wid);
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
            return projectLeaderRepository.existsByProject_IdAndWorker_Id(projectId, wid);
        }
        private boolean isAdmin(Authentication auth) {
            return auth.getAuthorities().stream()
                            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        }
        private boolean isAuth(Authentication auth) {
            return auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String);
        }
}