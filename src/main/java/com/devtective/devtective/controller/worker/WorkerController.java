package com.devtective.devtective.controller.worker;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.dominio.worker.WorkerRequestDTO;
import com.devtective.devtective.dominio.worker.WorkerResponseDTO;
import com.devtective.devtective.service.worker.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workers")
public class WorkerController {

    @Autowired
    WorkerService workerService;

    @PostMapping("/create")
    public ResponseEntity<WorkerResponseDTO> createWorker(@RequestBody WorkerRequestDTO workerDTO) {
        WorkerResponseDTO response = workerService.createWorker(workerDTO);
        return ResponseEntity.ok(response);
    }

}
