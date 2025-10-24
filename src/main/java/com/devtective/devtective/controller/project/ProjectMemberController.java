package com.devtective.devtective.controller.project;

import com.devtective.devtective.dominio.project.MemberDTO;
import com.devtective.devtective.service.project.ProjectMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectMemberController {

    private final ProjectMemberService service;

    public ProjectMemberController(ProjectMemberService service) {
        this.service = service;
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<MemberDTO>> listMembers(@PathVariable UUID projectId) {
        return ResponseEntity.ok(service.listMembers(projectId));
    }
}
