package com.devtective.devtective.service.project;

import com.devtective.devtective.dominio.project.*;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.dominio.worker.WorkerResponseDTO;
import com.devtective.devtective.dominio.workspace.Workspace;
import com.devtective.devtective.dominio.workspace.WorkspaceDTO;
import com.devtective.devtective.exception.NotFoundException;
import com.devtective.devtective.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectLeaderRepository projectLeaderRepository;

    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        System.out.println("Project service");
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

    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO dto, AppUser me) {
        Project project = new Project();

        project.setName(dto.name());
        project.setDescription(dto.description());
        project.setStartDate(dto.startDate());
        project.setEndDate(dto.endDate());

       Long createdById = dto.createdById();
       Worker projectCreator = null;
       if (createdById == null) {
           projectCreator = workerRepository.findByUserId(me);
       }
       else {
           projectCreator = workerRepository.findById(createdById)
                   .orElseThrow(() -> new NotFoundException("Worker with ID: " + dto.createdById() + " not found"));
       }

        project.setCreatedBy(projectCreator);

       Workspace wSpace = workspaceRepository.findByPublicId(dto.workspacePublicId());

       project.setWorkspace(wSpace);
       Project newProject = projectRepository.save(project);

        List<Worker> leaders = workerRepository.findWorkersByUserPublicIdIn(dto.leaderPublicIds());
        List<Worker> members = workerRepository.findWorkersByUserPublicIdIn(dto.memberPublicIds());

        List<ProjectLeader> projectLeaders = new ArrayList<>();
        List<ProjectMember> projectMembers = new ArrayList<>();

        for (Worker leader : leaders) {
            ProjectLeader newLeader = new ProjectLeader();
            newLeader.setProject(newProject);
            newLeader.setWorker(leader);
            newLeader.setWorkspaceId(wSpace.getId());
            projectLeaders.add(newLeader);
        }
        for (Worker member : members) {
            ProjectMember newMember = new ProjectMember();
            newMember.setProject(newProject);
            newMember.setWorker(member);
            newMember.setWorkspaceId(wSpace.getId());
            projectMembers.add(newMember);
        }

        ProjectMember pmCreator = new ProjectMember(newProject, projectCreator, wSpace.getId());
        projectMembers.add(pmCreator);
        System.out.println("*******PROJECT MEMBERS: " + pmCreator);

        projectLeaderRepository.saveAll(projectLeaders);
        projectMemberRepository.saveAll(projectMembers);

        ProjectResponseDTO response = convertToDTO(newProject);

       return response;
    }

    public ProjectResponseDTO updateProject(Long projectId, ProjectRequestDTO dto) {

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
    public Project getProjectByPublicId(UUID publicId) {
        Project project = projectRepository.findByPublicId(publicId);
        if (project == null) {
            throw new NotFoundException("Project with ID: " + publicId + " not found");
        }
        return project;
    }

    public ProjectResponseDTO getProjectResponseByPublicId(UUID publicId) {
        Project project = projectRepository.findByPublicId(publicId);
        return convertToDTO(project);
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

        List<Project> owned = projectRepository.findAllByCreatedById(workerId);

        List<Long> memberProjectIds = projectMemberRepository
                .findAllByWorkerId(workerId)
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
                project.getPublicId(),
                project.getName(),
                project.getDescription(),
                project.getUrl(),
                project.getStartDate(),
                project.getEndDate(),
                project.getCreatedBy() != null ? project.getCreatedBy().getFirstName() : null,
                convertToWorkspaceDTO(project.getWorkspace())
        );
    }
    private WorkspaceDTO convertToWorkspaceDTO(Workspace w) {
        return new WorkspaceDTO(w.getPublicId(), w.getName());
    }
}
