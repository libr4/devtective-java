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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {


    @Autowired
    public TaskService taskService;
    @Autowired
    public ProjectService projectService;
    @GetMapping
    ResponseEntity<List<Task>> getAllTasks() {
        List<Task> allTasks = taskService.getAllTasks();
        return ResponseEntity.ok(allTasks);
    }
    @PostMapping
    ResponseEntity<Task> createTask(@RequestBody TaskRequestDTO taskRequest) {
        System.out.println(taskRequest);
        Task createdTask = taskService.createTask(taskRequest);
        System.out.println(createdTask);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("{projectId}/{taskNumber:[0-9]+}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long projectId, @PathVariable Long taskNumber) {
        Project project = projectService.getProjectById(projectId);
        Task task = taskService.findTask(project, taskNumber);
        TaskResponseDTO response = convertToDTO(task);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<TaskResponseDTO> updateTask(@RequestBody TaskRequestDTO taskRequest) {
        Project project = projectService.getProjectById(taskRequest.projectId());
        Task updatedTask = taskService.updateTask(taskRequest);
        TaskResponseDTO response = convertToDTO(updatedTask);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{projectId}/{taskNumber:[0-9]+}")
    public ResponseEntity<String> deleteTask(@PathVariable Long projectId, @PathVariable Long taskNumber) {
        taskService.deleteByProjectIdAndTaskNumber(projectId, taskNumber);
        return ResponseEntity.noContent().build();
    }


    private List<TaskResponseDTO> convertToDTOList(List<Task> tasks) {
        return tasks.stream()
                .map(task -> convertToDTO(task))
                .collect(Collectors.toList());
    }

    private TaskResponseDTO convertToDTO(Task task) {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(task.getTitle(), task.getDescription(), task.getTaskStatus().getName(), task.getTaskPriority().getName(),
                task.getTaskType().getName(), task.getProject().getName(), task.getTechnology(),
                task.getAssignedTo().getFirstName(), task.getCreatedBy().getFirstName(), task.getDeadline());
        return taskResponseDTO;
    }

}
