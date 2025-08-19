package com.devtective.devtective.service.project;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.project.ProjectRequestDTO;
import com.devtective.devtective.dominio.project.ProjectResponseDTO;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.dominio.worker.WorkerResponseDTO;
import com.devtective.devtective.exception.NotFoundException;
import com.devtective.devtective.repository.ProjectMemberRepository;
import com.devtective.devtective.repository.ProjectRepository;
import com.devtective.devtective.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;

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

    public List<ProjectResponseDTO> getProjectsFor(AppUser me) {

        boolean isAdmin = me.getAuthorities().stream()
            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            return convertToDTOList(projectRepository.findAll());
        }

        Worker worker = workerRepository.findByUserId(me);
        if (worker == null) {
            return List.of();
        }
        Long workerId = worker.getId();

        List<Project> owned = projectRepository.findAllByCreatedBy_Id(workerId);

        List<Long> memberProjectIds = projectMemberRepository
                .findAllByWorker_Id(workerId)
                .stream()
                .map(pm -> pm.getProject().getId())
                .distinct()
                .toList();

        List<Project> memberProjects = memberProjectIds.isEmpty()
                        ? List.of()
                        : projectRepository.findAllById(memberProjectIds);

        List<Project> combined = new ArrayList<>(owned);
        for (Project p : memberProjects) {
            if (owned.stream().noneMatch(o -> o.getId() == p.getId())) {
                    combined.add(p);
            }
        }
        return convertToDTOList(combined);
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
