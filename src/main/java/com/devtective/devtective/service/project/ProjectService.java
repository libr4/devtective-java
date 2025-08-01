package com.devtective.devtective.service.project;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.project.ProjectRequestDTO;
import com.devtective.devtective.dominio.project.ProjectResponseDTO;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.dominio.worker.WorkerResponseDTO;
import com.devtective.devtective.exception.NotFoundException;
import com.devtective.devtective.repository.ProjectRepository;
import com.devtective.devtective.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WorkerRepository workerRepository;

    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        Project project = new Project();

        project.setName(dto.name());
        project.setDescription(dto.description());
        project.setStartDate(dto.startDate());
        project.setEndDate(dto.endDate());

        Worker projectCreator = workerRepository.findById(dto.createdById())
                .orElseThrow(() -> new NotFoundException("Worker with ID: " + dto.createdById() + " not found"));

        project.setCreatedBy(projectCreator);

        Project newProject = projectRepository.save(project);
        ProjectResponseDTO response = convertToDTO(newProject);
        return response;
    }

    public ProjectResponseDTO updateProject(ProjectRequestDTO dto) {
        Long projectId = dto.id();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project with ID: " + projectId + " not found"));
        project.setName(dto.name());
        project.setDescription(dto.description());
        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }

    public List<ProjectResponseDTO> getAllProjects() {
        List<Project> projects =  projectRepository.findAll();
        return convertToDTOList(projects);
    }

    public Project getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project with ID: " + id + " not found"));
        return project;
    }

    public ProjectResponseDTO getProjectResponseById(Long id) {
        Project project = getProjectById(id);
        return convertToDTO(project);
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project with ID: " + id + " not found"));
        projectRepository.delete(project);
    }

    public List<ProjectResponseDTO> convertToDTOList(List<Project> projects) {
        return projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProjectResponseDTO convertToDTO(Project project) {
        return new ProjectResponseDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getUrl(),
                project.getStartDate(),
                project.getEndDate(),
                project.getCreatedBy() != null ? project.getCreatedBy().getFirstName() : null
        );
    }
}
