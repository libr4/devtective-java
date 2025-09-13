package com.devtective.devtective.dominio.project;

import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.dominio.workspace.WorkspaceMember;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "project_leader", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "worker_id"}))
public class ProjectLeader implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leader_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_member_id", nullable = false)
    private WorkspaceMember workspaceMember;


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public WorkspaceMember getWorkspaceMember() {
        return workspaceMember;
    }

    public void setWorkspaceMember(WorkspaceMember workspaceMember) {
        this.workspaceMember = workspaceMember;
    }
}