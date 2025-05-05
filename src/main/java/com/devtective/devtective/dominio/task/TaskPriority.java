package com.devtective.devtective.dominio.task;

import jakarta.persistence.*;

@Entity
@Table(name = "task_priority")
public class TaskPriority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_priority_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // bellow, constructors, getters and setters only!
    public TaskPriority() {}

    public TaskPriority(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setTaskPriorityId(Long taskPriorityId) {
        this.id = taskPriorityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
