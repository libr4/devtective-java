package com.devtective.devtective.controller.project;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.task.Task;
import com.devtective.devtective.dominio.task.TaskRequestDTO;
import com.devtective.devtective.dominio.task.TaskResponseDTO;
import com.devtective.devtective.service.project.ProjectService;
import com.devtective.devtective.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
public class ProjectTaskController {

    @Autowired TaskService taskService;
    @Autowired ProjectService projectService;

    @GetMapping
    @PreAuthorize("@perm.canReadOrCreateTask(authentication, #projectId)")
    public ResponseEntity<List<TaskResponseDTO>> listByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.listByProject(projectId));
    }

    @PostMapping
    @PreAuthorize("@perm.canReadOrCreateTask(authentication, #projectId)")
    public ResponseEntity<TaskResponseDTO> create(@PathVariable Long projectId,
                                                  @RequestBody TaskRequestDTO body) {
        TaskRequestDTO req = body.withProjectId(projectId);
        return ResponseEntity.ok(taskService.createTaskResponseDTO(req));
    }

    @GetMapping("{taskNumber:[0-9]+}")
    @PreAuthorize("@perm.canReadOrCreateTask(authentication, #projectId)")
    public ResponseEntity<TaskResponseDTO> getOne(@PathVariable Long projectId,
                                                  @PathVariable Long taskNumber) {
        Project project = projectService.getProjectById(projectId);
        return ResponseEntity.ok(taskService.findTaskResponse(project, taskNumber));
    }

    @PutMapping("{taskNumber:[0-9]+}")
    @PreAuthorize("@perm.canModifyTask(authentication, #projectId)")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long projectId,
                                                  @PathVariable Long taskNumber,
                                                  @RequestBody TaskRequestDTO body) {
        TaskRequestDTO req = body.withProjectIdAndTaskNumber(projectId, taskNumber);
        return ResponseEntity.ok(taskService.updateTaskResponseDTO(req));
    }

    @DeleteMapping("{taskNumber:[0-9]+}")
    @PreAuthorize("@perm.canModifyTask(authentication, #projectId)")
    public ResponseEntity<Void> delete(@PathVariable Long projectId,
                                       @PathVariable Long taskNumber) {
        taskService.deleteByProjectIdAndTaskNumber(projectId, taskNumber);
        return ResponseEntity.noContent().build();
    }
}
