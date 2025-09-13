package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.dominio.workspace.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
        Workspace findByPublicId(UUID publicId);
        @Query("""
         select distinct wm.workspace
         from WorkspaceMember wm
         join wm.worker wr
         join wr.userId u
         where u.publicId = :publicUserId
         """)
        List<Workspace> findWorkspacesByUserPublicId(@Param("publicUserId") UUID publicUserId);
}
