package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.dominio.workspace.Workspace;
import com.devtective.devtective.dominio.workspace.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
        WorkspaceMember findByWorkspaceIdAndWorkerId(Long workspaceId, Long workerId);
        List<WorkspaceMember> findByWorker(Worker worker);
        boolean existsByWorkspaceIdAndWorkerId(Long workspaceId, Long workerId);
}
