package com.devtective.devtective.dominio.task;

import jakarta.persistence.*;

@Entity
@Table(name = "task_type")
public class TaskType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_type_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // bellow constructors, setters and getters only
    public TaskType() {}

    public TaskType(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
