package com.devtective.devtective.service.task;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.task.*;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.exception.NotFoundException;
import com.devtective.devtective.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        long taskCount = repository.countByProjectId(project.getId());
        newTask.setTaskNumber(taskCount + 1);

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

        return repository.save(newTask);
    }

    public TaskResponseDTO createTaskResponseDTO(TaskRequestDTO dto) {
        Task task = createTask(dto);
        return convertToDTO(task);

    }

    @Transactional
    public Task updateTask(TaskRequestDTO taskRequestDTO) {

        Project project = projectRepository.findById(taskRequestDTO.projectId()).orElseThrow(() -> new NotFoundException("Project with ID: " + taskRequestDTO.projectId() + " not found"));

        Task task = repository.findByProjectAndTaskNumber(project, taskRequestDTO.taskNumber());
        if (task == null) return null;

        if (taskRequestDTO.title() != null && !taskRequestDTO.title().isBlank()) {
            task.setTitle(taskRequestDTO.title());
        }
        if (taskRequestDTO.description() != null) {
            task.setDescription(taskRequestDTO.description());
        }
        if (taskRequestDTO.technology() != null) {
            task.setTechnology(taskRequestDTO.technology());
        }
        if (taskRequestDTO.deadline() != null) {
            task.setDeadline(taskRequestDTO.deadline());
        }

        if (taskRequestDTO.taskStatusId() != null) {
            taskStatusRepository.findById(taskRequestDTO.taskStatusId())
                    .ifPresent(task::setTaskStatus);
        }

        if (taskRequestDTO.taskPriorityId() != null) {
            taskPriorityRepository.findById(taskRequestDTO.taskPriorityId())
                    .ifPresent(task::setTaskPriority);
        }

        if (taskRequestDTO.taskTypeId() != null) {
            taskTypeRepository.findById(taskRequestDTO.taskTypeId())
                    .ifPresent(task::setTaskType);
        }

        if (taskRequestDTO.assignedToId() != null) {
            workerRepository.findById(taskRequestDTO.assignedToId())
                    .ifPresent(task::setAssignedTo);
        }

        if (taskRequestDTO.createdById() != null) {
            workerRepository.findById(taskRequestDTO.createdById())
                    .ifPresent(task::setCreatedBy);
        }

        return repository.save(task);
    }
    public TaskResponseDTO updateTaskResponseDTO(TaskRequestDTO dto) {
        Task updatedTask = updateTask(dto);
        TaskResponseDTO response = convertToDTO(updatedTask);
        return response;
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }
    public Task findTask(Project project, Long taskNumber) {
        return repository.findByProjectAndTaskNumber(project, taskNumber);
    }

    public TaskResponseDTO findTaskResponse(Project project, Long taskNumber) {
        Task task = findTask(project, taskNumber);

        if (task == null)  {
            throw new NotFoundException("Task wih number: " + taskNumber + " not found");

        }

        return convertToDTO(task);
    }

    public void deleteByProjectIdAndTaskNumber(Long projectId, Long taskNumber) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project with ID: " + projectId + " not found"));

        Task task =  repository.findByProjectAndTaskNumber(project, taskNumber);
        if (task == null)  {
            throw new NotFoundException("Task wih number: " + taskNumber + " not found");

        }
        repository.delete(task);
    }

    public List<TaskResponseDTO> listByProject(Long projectId) {
        List<Task> tasks = repository.findByProjectId(projectId);
        return tasks.stream().map(this::convertToDTO).toList();
    }

    public List<TaskResponseDTO> getAllTasksResponse() {
        List<Task> allTasks = getAllTasks();
        return convertToDTOList(allTasks);
    }

    public List<TaskResponseDTO> convertToDTOList(List<Task> tasks) {
        return tasks.stream()
                .map(task -> convertToDTO(task))
                .collect(Collectors.toList());
    }



    public TaskResponseDTO convertToDTO(Task task) {
        if (task == null) return null;

        String status   = (task.getTaskStatus()   != null) ? task.getTaskStatus().getName()      : null;
        String priority = (task.getTaskPriority() != null) ? task.getTaskPriority().getName()    : null;
        String type     = (task.getTaskType()     != null) ? task.getTaskType().getName()        : null;
        String project  = (task.getProject()      != null) ? task.getProject().getName()         : null;
        String assigned = (task.getAssignedTo()   != null) ? task.getAssignedTo().getFirstName() : null;
        String created  = (task.getCreatedBy()    != null) ? task.getCreatedBy().getFirstName()  : null;

        return new TaskResponseDTO(
                task.getTitle(),
                task.getDescription(),
                status,
                priority,
                type,
                project,
                task.getTechnology(),
                assigned,
                created,
                task.getDeadline(),
                task.getTaskNumber()
        );
    }

}
