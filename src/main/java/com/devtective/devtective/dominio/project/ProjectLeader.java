package com.devtective.devtective.dominio.project;

import com.devtective.devtective.dominio.worker.Worker;
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
}