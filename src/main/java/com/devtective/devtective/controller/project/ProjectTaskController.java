package com.devtective.devtective.controller.project;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.task.Task;
import com.devtective.devtective.dominio.task.TaskRequestDTO;
import com.devtective.devtective.dominio.task.TaskResponseDTO;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.service.project.ProjectService;
import com.devtective.devtective.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects/{projectPublicId}/tasks")
public class ProjectTaskController {

    @Autowired TaskService taskService;
    @Autowired ProjectService projectService;

    @GetMapping
    @PreAuthorize("@perm.canReadOrCreateTask(authentication, #projectPublicId)")
    public ResponseEntity<List<TaskResponseDTO>> listByProject(@PathVariable UUID projectPublicId) {
        return ResponseEntity.ok(taskService.listByProject(projectPublicId));
    }

    @PostMapping
    @PreAuthorize("@perm.canReadOrCreateTask(authentication, #projectPublicId)")
    public ResponseEntity<TaskResponseDTO> create(@PathVariable UUID projectPublicId,
                                                  @RequestBody TaskRequestDTO body,
                                                  @AuthenticationPrincipal AppUser me) {
        TaskRequestDTO req = body.withProjectId(projectPublicId);
        return ResponseEntity.ok(taskService.createTaskResponseDTO(me, req));
    }

    @GetMapping("{taskNumber:[0-9]+}")
    @PreAuthorize("@perm.canReadOrCreateTask(authentication, #projectPublicId)")
    public ResponseEntity<TaskResponseDTO> getOne(@PathVariable UUID projectPublicId,
                                                  @PathVariable Long taskNumber) {
        Project project = projectService.getProjectByPublicId(projectPublicId);
        return ResponseEntity.ok(taskService.findTaskResponse(project, taskNumber));
    }

    @PutMapping("{taskNumber:[0-9]+}")
    @PreAuthorize("@perm.canModifyTask(authentication, #projectId)")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable UUID projectId,
                                                  @PathVariable Long taskNumber,
                                                  @RequestBody TaskRequestDTO body) {
        TaskRequestDTO req = body.withProjectPublicIdAndTaskNumber(projectId, taskNumber);
        return ResponseEntity.ok(taskService.updateTaskResponseDTO(req));
    }

    @DeleteMapping("{taskNumber:[0-9]+}")
    @PreAuthorize("@perm.canModifyTask(authentication, #projectPublic)")
    public ResponseEntity<Void> delete(@PathVariable UUID projectPublic,
                                       @PathVariable Long taskNumber) {
        taskService.deleteByProjectIdAndTaskNumber(projectPublic, taskNumber);
        return ResponseEntity.noContent().build();
    }
}
