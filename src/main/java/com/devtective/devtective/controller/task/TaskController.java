package com.devtective.devtective.controller.task;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.task.Task;
import com.devtective.devtective.dominio.task.TaskRequestDTO;
import com.devtective.devtective.dominio.task.TaskResponseDTO;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.dominio.user.UserResponseDTO;
import com.devtective.devtective.repository.TaskRepository;
import com.devtective.devtective.service.project.ProjectService;
import com.devtective.devtective.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired public TaskService taskService;
    @Autowired public ProjectService projectService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        List<TaskResponseDTO> response = taskService.getAllTasksResponse();
        return ResponseEntity.ok(response);
    }

    // Create: members/leads/owner/admin of the target project
    @PostMapping
    @PreAuthorize("@perm.canReadOrCreateTask(authentication, #taskRequest.projectId)")
    ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskRequestDTO taskRequest) {
        TaskResponseDTO createdTask = taskService.createTaskResponseDTO(taskRequest);
        return ResponseEntity.ok(createdTask);
    }

    // Read single task: members/leads/owner/admin
    @GetMapping("{projectId}/{taskNumber:[0-9]+}")
    @PreAuthorize("@perm.canReadOrCreateTask(authentication, #projectId)")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long projectId, @PathVariable Long taskNumber) {
        Project project = projectService.getProjectById(projectId);
        TaskResponseDTO response = taskService.findTaskResponse(project, taskNumber);
        return ResponseEntity.ok(response);
    }

    // Update: leads/owner/admin (projectId comes from body)
    @PutMapping
    @PreAuthorize("@perm.canModifyTask(authentication, #taskRequest.projectId)")
    public ResponseEntity<TaskResponseDTO> updateTask(@RequestBody TaskRequestDTO taskRequest) {
        TaskResponseDTO response = taskService.updateTaskResponseDTO(taskRequest);
        return ResponseEntity.ok(response);
    }

    // Delete: leads/owner/admin
    @DeleteMapping("{projectId}/{taskNumber:[0-9]+}")
    @PreAuthorize("@perm.canModifyTask(authentication, #projectId)")
    public ResponseEntity<String> deleteTask(@PathVariable Long projectId, @PathVariable Long taskNumber) {
        taskService.deleteByProjectIdAndTaskNumber(projectId, taskNumber);
        return ResponseEntity.noContent().build();
    }
}
