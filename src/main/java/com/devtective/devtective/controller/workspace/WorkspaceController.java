package com.devtective.devtective.controller.workspace;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.workspace.Workspace;
import com.devtective.devtective.dominio.workspace.WorkspaceDTO;
import com.devtective.devtective.service.workspace.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("/users/{userPublicId}")
    public ResponseEntity<List<WorkspaceDTO>> getWorkspacesByUserPublicId(
            @PathVariable("userPublicId") UUID userPublicId) {
        List<Workspace> workspaces = workspaceService.getWorkspacesByUserPublicId(userPublicId);
        List<WorkspaceDTO> response = workspaces.stream().map(WorkspaceDTO::from).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<WorkspaceDTO> createWorkspace(@RequestBody @Valid WorkspaceDTO data, @AuthenticationPrincipal AppUser me) {
        WorkspaceDTO response = workspaceService.createWorkspace(data, me);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<WorkspaceDTO>> getMyWorkspaces(
            @AuthenticationPrincipal AppUser me) {
        List<Workspace> workspaces = workspaceService.getWorkspacesByUserPublicId(me.getPublicId());
        return ResponseEntity.ok(workspaces.stream().map(WorkspaceDTO::from).toList());
    }

}