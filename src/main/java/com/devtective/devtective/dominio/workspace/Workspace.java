package com.devtective.devtective.dominio.workspace;

import jakarta.persistence.*;

import java.util.UUID;
import java.time.Instant;

@Entity
@Table(name = "workspace")
public class Workspace {

    public Workspace() {
    }
    public Workspace(String name) {
        this.name = name;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @PrePersist
    void ensurePublicId() {
        if (publicId == null) publicId = UUID.randomUUID();
    }

    @Column(nullable = false) private String name;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}