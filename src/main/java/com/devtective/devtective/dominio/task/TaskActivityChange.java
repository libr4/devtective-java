package com.devtective.devtective.dominio.task;

import jakarta.persistence.*;

@Entity
@Table(name = "task_activity_changes")
public class TaskActivityChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_activity_changes_id")
    private Long taskActivityChangesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_activity_id")
    private TaskActivity taskActivity;

    @Column(name = "field", nullable = false)
    private String field;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    // Getters and setters
    public Long getTaskActivityChangesId() {
        return taskActivityChangesId;
    }

    public void setTaskActivityChangesId(Long taskActivityChangesId) {
        this.taskActivityChangesId = taskActivityChangesId;
    }

    public TaskActivity getTaskActivity() {
        return taskActivity;
    }

    public void setTaskActivity(TaskActivity taskActivity) {
        this.taskActivity = taskActivity;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}