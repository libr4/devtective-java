package com.devtective.devtective.controller.task;

import com.devtective.devtective.dominio.task.Task;
import com.devtective.devtective.dominio.task.TaskRequestDTO;
import com.devtective.devtective.repository.TaskRepository;
import com.devtective.devtective.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {


    @Autowired
    public TaskService taskService;
    @PostMapping("/")
    ResponseEntity<Task> createTask(@RequestBody TaskRequestDTO taskRequest) {
        System.out.println(taskRequest);
        Task createdTask = taskService.createTask(taskRequest);
        System.out.println(createdTask);
        return ResponseEntity.ok(createdTask);
    }

}
