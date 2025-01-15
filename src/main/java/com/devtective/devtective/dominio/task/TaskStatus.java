package com.devtective.devtective.dominio.task;

import jakarta.persistence.*;

@Entity
@Table(name = "task_status")
public class TaskStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_status_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // Constructors, getters, and setters
    public TaskStatus() {}

    public TaskStatus(String name) {
        this.name = name;
    }

    public Long getTaskStatusId() {
        return id;
    }

    public void setTaskStatusId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
