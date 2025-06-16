package com.devtective.devtective.service.project;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository repository;
    public List<Project> getAllProjects() {
        List<Project> allProjects = repository.findAll();
        return allProjects;
    }

    public Project getProject(Long id) {
        Project project = repository.findById(id).orElse(null);
        return project;
    }

}
