package com.devtective.devtective.dominio.workspace;

import com.devtective.devtective.dominio.worker.Worker;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspace_member", uniqueConstraints = @UniqueConstraint(name="uq_ws_member", columnNames={"workspace_id","worker_id"}))
@RequiredArgsConstructor
public class WorkspaceMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_member_id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;
    @PrePersist
    void ensurePublicId() {
        if (publicId == null) publicId = UUID.randomUUID();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    public WorkspaceMember(Workspace workspace, Worker worker) {
        this.workspace = workspace;
        this.worker = worker;
    }

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();

    // just getters and setters below...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }
}
