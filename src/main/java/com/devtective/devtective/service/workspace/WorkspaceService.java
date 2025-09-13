package com.devtective.devtective.service.workspace;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.dominio.workspace.Workspace;
import com.devtective.devtective.dominio.workspace.WorkspaceDTO;
import com.devtective.devtective.dominio.workspace.WorkspaceMember;
import com.devtective.devtective.exception.NotFoundException;
import com.devtective.devtective.repository.UserRepository;
import com.devtective.devtective.repository.WorkerRepository;
import com.devtective.devtective.repository.WorkspaceMemberRepository;
import com.devtective.devtective.repository.WorkspaceRepository;
import com.devtective.devtective.service.user.UserService;
import com.devtective.devtective.service.worker.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceMemberRepository wpmRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    public List<Workspace> getWorkspacesByUserPublicId(UUID userPublicId) {
        List<Workspace> workspaces = workspaceRepository.findWorkspacesByUserPublicId(userPublicId);
        return workspaces;
    }


    @Transactional
    public WorkspaceDTO createWorkspace(WorkspaceDTO data, AppUser me) {

        Workspace newWs = new Workspace(data.name().trim());
        Workspace w = workspaceRepository.save(newWs);

        Worker worker = workerRepository.findByUserId(me);
        if (worker == null) {
            throw new NotFoundException("User not found!");
        }

        WorkspaceMember wsm = new WorkspaceMember();
        wsm.setWorkspace(w);
        wsm.setWorker(worker);

        workspaceMemberRepository.save(wsm);
        WorkspaceDTO response = WorkspaceDTO.from(w);

        return response;
    }
}
