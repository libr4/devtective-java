package com.devtective.devtective.service.worker;

import com.devtective.devtective.dominio.task.Task;
import com.devtective.devtective.dominio.task.TaskResponseDTO;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.Role;
import com.devtective.devtective.dominio.user.RoleConstants;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.dominio.worker.Position;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.dominio.worker.WorkerRequestDTO;
import com.devtective.devtective.dominio.worker.WorkerResponseDTO;
import com.devtective.devtective.repository.UserRepository;
import com.devtective.devtective.repository.WorkerRepository;
import com.devtective.devtective.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public WorkerResponseDTO createWorker(WorkerRequestDTO data) {

        Worker worker = fromDTOtoWorker(data);
        Worker createdWorker = workerRepository.save(worker);
        WorkerResponseDTO response = fromWorkerToResponseDTO(createdWorker);
        return response;
    }

    public Worker fromDTOtoWorker(WorkerRequestDTO data) {

        Worker worker = new Worker();
        worker.setFirstName(data.firstName());
        worker.setLastName(data.lastName());
        System.out.println("THIS IS WORKER DTO: " + data);

        AppUser user = userRepository.findByUserId(data.userId());
        System.out.println("THIS IS USER: " + user);
        worker.setUserId(user);

        Position position = new Position(data.positionId());
        worker.setPositionId(position);
        System.out.println("THIS IS WORKER: " + worker);

        return worker;

    }

    public WorkerResponseDTO fromWorkerToResponseDTO(Worker worker) {
        Position p = worker.getPositionId();
        AppUser u = worker.getUserId();
        Long posId = null;
        if (p != null) {
            posId = p.getId();
        }

        Long uId = null;
        if (u != null) {
            uId = u.getUserId();
        }

        WorkerResponseDTO workerResponseDTO = new WorkerResponseDTO(
                worker.getId(), worker.getFirstName(),
                worker.getLastName(), posId,
                uId
        );
        return workerResponseDTO;
    }

}