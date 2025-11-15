package com.devtective.devtective.controller.invite;

import org.springframework.web.bind.annotation.RestController;

import com.devtective.devtective.dominio.project.ProjectResponseDTO;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.service.project.ProjectService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/v1/invite")
@RequiredArgsConstructor
public class InviteController {

    private final ProjectService projectService;

    @PostMapping("/{inviteId}")
    public ResponseEntity<ProjectResponseDTO> joinProject(@PathVariable UUID inviteId, @AuthenticationPrincipal AppUser me) {
        return ResponseEntity.ok(projectService.joinProject(inviteId, me));
    }
    
}
