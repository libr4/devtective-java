package com.devtective.devtective.service.task;

import com.devtective.devtective.common.dto.IdNameDTO;
import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.task.*;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.exception.NotFoundException;
import com.devtective.devtective.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

        Project project = projectRepository.findByPublicId(task.projectPublicId());
        newTask.setProject(project);

        long taskCount = repository.countByProjectId(project.getId());
        newTask.setTaskNumber(taskCount + 1);

        Worker createdBy = workerRepository.findByUserId_PublicId(task.createdById());
        newTask.setCreatedBy(createdBy);

        UUID assignedToId = task.assignedToId() == null ? task.assignedToId() : task.createdById();
        Worker assignedTo = workerRepository.findByUserId_PublicId(assignedToId);
        newTask.setAssignedTo(assignedTo);

        TaskPriority priority = null;
        if (task.taskPriorityId() != null) {
            priority = taskPriorityRepository.findById(task.taskPriorityId())
                    .orElseThrow(() -> new NotFoundException("Task Priority with ID: " + task.taskPriorityId() + " not found"));
        }
        newTask.setTaskPriority(priority);

        TaskStatus status = null;
        if (task.taskStatusId() != null) {
            status = taskStatusRepository.findById(task.taskStatusId())
                    .orElseThrow(() -> new NotFoundException("Task Status with ID: " + task.taskStatusId() + " not found"));
        }
        newTask.setTaskStatus(status);

        TaskType type = null;
        if (task.taskTypeId() != null) {
            type = taskTypeRepository.findById(task.taskTypeId())
                    .orElseThrow(() -> new NotFoundException("Task Type with ID: " + task.taskTypeId() + " not found"));
        }
        newTask.setTaskType(type);

        return repository.save(newTask);
    }

    public TaskResponseDTO createTaskResponseDTO(AppUser me, TaskRequestDTO dto) {
        TaskRequestDTO withCreatedBy = dto.withCreatedById(me.getPublicId());
        Task task = createTask(withCreatedBy);
        TaskResponseDTO response =  convertToDTO(task);
        return response;
    }

    @Transactional
    public Task updateTask(TaskRequestDTO taskRequestDTO) {

        Project project = projectRepository.findByPublicId(taskRequestDTO.projectPublicId());

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
            Worker assignedTo = workerRepository.findByUserId_PublicId(taskRequestDTO.assignedToId());
            task.setAssignedTo(assignedTo);
        }

        if (taskRequestDTO.createdById() != null) {
            Worker createdBy = workerRepository.findByUserId_PublicId(taskRequestDTO.createdById());
            task.setCreatedBy(createdBy);
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

        TaskResponseDTO response =  convertToDTO(task);
        return response;
    }

    public void deleteByProjectIdAndTaskNumber(UUID projectId, Long taskNumber) {
        Project project = projectRepository.findByPublicId(projectId);

        Task task =  repository.findByProjectAndTaskNumber(project, taskNumber);
        if (task == null)  {
            throw new NotFoundException("Task wih number: " + taskNumber + " not found");

        }
        repository.delete(task);
    }

    public List<TaskResponseDTO> listByProject(UUID projectPublicId) {
        List<Task> tasks = repository.findByProjectPublicId(projectPublicId);
        return tasks.stream().map(this::convertToDTO).toList();
    }

    public List<TaskResponseDTO> listByProjectAndParams(UUID projectPublicId, MultiValueMap<String, String> params) {
        String q =params.getFirst("q");
        q = q != null ? "%" + q + "%" : null;
        List<String> usernames = params.get("assignedTo");
        List<String> priorities = params.get("priority");
        List<String> types = params.get("type");
        List<String> statuses = params.get("status");

        List<Task> tasks = repository.findByProjectPublicIdAndParams(
            projectPublicId, 
            q,
            usernames, 
            types, 
            priorities, 
            statuses);
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

    @Transactional(readOnly = true)
    public List<IdNameDTO> getTypes() {
        return taskTypeRepository.findAllByOrderByNameAsc()
                .stream().map(p -> new IdNameDTO(p.id(), p.name())).toList();
    }
    @Transactional(readOnly = true)
    public List<IdNameDTO> getPriorities() {
        return taskPriorityRepository.findAllByOrderByNameAsc()
                .stream().map(p -> new IdNameDTO(p.id(), p.name())).toList();
    }
    @Transactional(readOnly = true)
    public List<IdNameDTO> getStatuses() {
        return taskStatusRepository.findAllByOrderByNameAsc()
                .stream().map(p -> new IdNameDTO(p.id(), p.name())).toList();
    }

}
