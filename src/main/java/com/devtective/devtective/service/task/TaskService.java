package com.devtective.devtective.service.task;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.task.*;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.exception.NotFoundException;
import com.devtective.devtective.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    TaskRepository repository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    WorkerRepository workerRepository;
    @Autowired
    TaskPriorityRepository taskPriorityRepository;
    @Autowired
    TaskStatusRepository taskStatusRepository;
    @Autowired
    TaskTypeRepository taskTypeRepository;

    public Task createTask(TaskRequestDTO task) {
        Task newTask = new Task();

        newTask.setTitle(task.title());
        newTask.setDescription(task.description());
        newTask.setTechnology(task.technology());
        newTask.setDeadline(task.deadline());

        newTask.setCreatedAt(LocalDateTime.now());
        newTask.setUpdatedAt(LocalDateTime.now());

        Project project = projectRepository.findById(task.projectId())
                .orElseThrow(() -> new NotFoundException("Project with ID: " + task.projectId() + " not found"));
        newTask.setProject(project);

        Worker createdBy = workerRepository.findById(task.createdById())
                .orElseThrow(() -> new NotFoundException("Worker (creator) with ID: " + task.createdById() + " not found"));
        newTask.setCreatedBy(createdBy);

        Worker assignedTo = workerRepository.findById(task.assignedToId())
                .orElseThrow(() -> new NotFoundException("Worker with ID: " + task.assignedToId() + " not found"));
        newTask.setAssignedTo(assignedTo);

        TaskPriority priority = taskPriorityRepository.findById(task.taskPriorityId())
                .orElseThrow(() -> new NotFoundException("Task Priority with ID: " + task.taskPriorityId() + " not found"));
        newTask.setTaskPriority(priority);

        TaskStatus status = taskStatusRepository.findById(task.taskStatusId())
                .orElseThrow(() -> new NotFoundException("Task Status with ID: " + task.taskStatusId() + " not found"));
        newTask.setTaskStatus(status);

        TaskType type = taskTypeRepository.findById(task.taskTypeId())
                .orElseThrow(() -> new NotFoundException("Task Type with ID: " + task.taskTypeId() + " not found"));
        newTask.setTaskType(type);

        if (task.taskStatusId() != null) {
        }

        if (task.taskPriorityId() != null) {
        }

        if (task.taskTypeId() != null) {
        }

        if (task.projectId() != null) {
        }

        if (task.assignedToId() != null) {
        }

        if (task.createdById() != null) {
        }

        return repository.save(newTask);
    }

    public TaskResponseDTO createTaskResponseDTO(TaskRequestDTO dto) {
        Task task = createTask(dto);
        return convertToDTO(task);

    }

    public Task updateTask(TaskRequestDTO taskRequestDTO) {
        Project project = projectRepository.findById(taskRequestDTO.projectId()).orElse(null);

        if (project != null) {
            Task task = repository.findByProjectAndTaskNumber(project, taskRequestDTO.taskNumber());
            return repository.save(task);

        }
        return null;
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }
    public Task findTask(Project project, Long taskNumber) {
        return repository.findByProjectAndTaskNumber(project, taskNumber);
    }

    public void deleteByProjectIdAndTaskNumber(Long projectId, Long taskNumber) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project with ID: " + projectId + " not found"));

        Task task =  repository.findByProjectAndTaskNumber(project, taskNumber);
        if (task == null)  {
            throw new NotFoundException("Task wih number: " + taskNumber + " not found");

        }
        repository.delete(task);
    }

    public List<TaskResponseDTO> convertToDTOList(List<Task> tasks) {
        return tasks.stream()
                .map(task -> convertToDTO(task))
                .collect(Collectors.toList());
    }

    public TaskResponseDTO convertToDTO(Task task) {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(task.getTitle(), task.getDescription(), task.getTaskStatus().getName(), task.getTaskPriority().getName(),
                task.getTaskType().getName(), task.getProject().getName(), task.getTechnology(),
                task.getAssignedTo().getFirstName(), task.getCreatedBy().getFirstName(), task.getDeadline());
        return taskResponseDTO;
    }

}
