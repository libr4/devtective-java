package com.devtective.devtective.controller.project;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.project.ProjectRequestDTO;
import com.devtective.devtective.dominio.project.ProjectResponseDTO;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.service.project.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    //@GetMapping
    //public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        //List<ProjectResponseDTO> projects = projectService.getAllProjects();
        //return ResponseEntity.ok(projects);
    //}

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(@AuthenticationPrincipal AppUser me) {
        List<ProjectResponseDTO> projects = projectService.getProjectsFor(me);
        return ResponseEntity.ok(projects);
    }

    @PreAuthorize("@perm.projectMemberOrAdmin(authentication, #id)")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProject(@PathVariable Long id) {
        ProjectResponseDTO response = projectService.getProjectResponseById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody ProjectRequestDTO project) {
        System.out.println("Project controller");
        ProjectResponseDTO created = projectService.createProject(project);
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("@perm.ownerOrLeadOrAdmin(authentication, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            //@AuthenticationPrincipal AppUser me,
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDTO project) {
        ProjectResponseDTO updated = projectService.updateProject(id, project);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("@perm.ownerOrLeadOrAdmin(authentication, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}