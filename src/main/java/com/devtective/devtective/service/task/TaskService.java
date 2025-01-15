package com.devtective.devtective.service.task;

import com.devtective.devtective.dominio.task.*;
import com.devtective.devtective.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskService {

    @Autowired
    TaskRepository repository;
    public Task createTask(TaskRequestDTO task) {
        Task newTask = new Task();

        newTask.setTitle(task.title());
        newTask.setDescription(task.description());
        newTask.setTechnology(task.technology());
        newTask.setDeadline(task.deadline());

        newTask.setCreatedAt(LocalDateTime.now());
        newTask.setUpdatedAt(LocalDateTime.now());

        if (task.taskStatusId() != null) {
        }

        if (task.taskPriorityId() != null) {
        }

        if (task.taskTypeId() != null) {
        }

        if (task.projectId() != null) {
        }

        if (task.assignedToId() != null) {
        }

        if (task.createdById() != null) {
        }

        return repository.save(newTask);
    }

}
