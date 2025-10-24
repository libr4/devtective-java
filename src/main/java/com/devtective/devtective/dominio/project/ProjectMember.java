package com.devtective.devtective.dominio.project;

import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.dominio.workspace.WorkspaceMember;
import jakarta.persistence.*;

@Entity
@Table(name = "project_member")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    // bellow, getters, setters and constructors only
    public ProjectMember() {
    }

    public ProjectMember(Project project, Worker worker) {
        this.project = project;
        this.worker = worker;
    }

    public ProjectMember(Project project, Worker worker, Long workspaceId) {
        this.project = project;
        this.worker = worker;
        this.workspaceId = workspaceId;
    }

    public Long getMemberId() {
        return id;
    }

    public void setMemberId(Long id) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

}